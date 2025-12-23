import http from 'k6/http';
import ws from 'k6/ws';
import { check } from 'k6';

const VUS = __ENV.VUS ? parseInt(__ENV.VUS, 10) : 500;
const DURATION = __ENV.DURATION || '30s';
const USE_STEP_SCENARIO = __ENV.SCENARIO === 'step-bid';

const STEP_STAGE_MINUTES = 2;
const STEP_RATE_SWITCH_MS = __ENV.STEP_RATE_SWITCH_MS
  ? parseInt(__ENV.STEP_RATE_SWITCH_MS, 10)
  : 60000;
const STEP_FIRST_RPS = __ENV.STEP_FIRST_RPS ? parseFloat(__ENV.STEP_FIRST_RPS) : 2;
const STEP_SECOND_RPS = __ENV.STEP_SECOND_RPS ? parseFloat(__ENV.STEP_SECOND_RPS) : 4;
const STEP_FIRST_SEND_EVERY_MS = Math.round(1000 / STEP_FIRST_RPS);
const STEP_SECOND_SEND_EVERY_MS = Math.round(1000 / STEP_SECOND_RPS);

const STEP_SCENARIO_STAGES = [
  { vus: 50 },
  { vus: 100 },
  { vus: 150 },
  { vus: 200 },
  { vus: 250 },
  { vus: 300 },
  { vus: 350 },
  { vus: 400 },
];

function buildStepScenarios() {
  const scenarios = {};
  const stageSessionMs = STEP_STAGE_MINUTES * 60 * 1000;
  STEP_SCENARIO_STAGES.forEach((stage, index) => {
    scenarios[`step_${stage.vus}vus`] = {
      executor: 'constant-vus',
      vus: stage.vus,
      duration: `${STEP_STAGE_MINUTES}m`,
      startTime: `${index * STEP_STAGE_MINUTES}m`,
      env: {
        SESSION_MS: String(stageSessionMs),
        SCENARIO_VUS: String(stage.vus),
      },
    };
  });
  return scenarios;
}

export const options = {
  ...(USE_STEP_SCENARIO
    ? { scenarios: buildStepScenarios() }
    : { vus: VUS, duration: DURATION }),
};

const WS_BASE = __ENV.WS_BASE || 'ws://host.docker.internal:6600';
const HTTP_BASE = __ENV.HTTP_BASE || WS_BASE.replace(/^ws/i, 'http');
const AUCTION_ID = __ENV.AUCTION_ID || '11111111-1111-1111-1111-111111111111';
const DEBUG = __ENV.DEBUG === '1';
const LOG_WS_ERRORS = __ENV.LOG_WS_ERRORS === '1';
const LOG_INFO_SLOW_MS = __ENV.LOG_INFO_SLOW_MS
  ? parseInt(__ENV.LOG_INFO_SLOW_MS, 10)
  : 0;
const SEND_DELAY_MS = __ENV.SEND_DELAY_MS ? parseInt(__ENV.SEND_DELAY_MS, 10) : 0;
const SEND_EVERY_MS = __ENV.SEND_EVERY_MS ? parseInt(__ENV.SEND_EVERY_MS, 10) : 1000;
const SESSION_MS = __ENV.SESSION_MS ? parseInt(__ENV.SESSION_MS, 10) : 10000;
const BID_PRICE_BASE = __ENV.BID_PRICE_BASE ? parseFloat(__ENV.BID_PRICE_BASE) : 1000;
const BID_PRICE_STEP = __ENV.BID_PRICE_STEP ? parseFloat(__ENV.BID_PRICE_STEP) : 0.01;
const BID_PRICE_START = __ENV.BID_PRICE_START ? parseFloat(__ENV.BID_PRICE_START) : null;
const BID_PRICE_MODE = (__ENV.BID_PRICE_MODE || '').toLowerCase();
const VU_COUNT = __ENV.SCENARIO_VUS ? parseInt(__ENV.SCENARIO_VUS, 10) : VUS;

function buildQuery(params) {
  const parts = [];
  Object.keys(params).forEach((key) => {
    const value = params[key];
    if (value === undefined || value === null || value === '') {
      return;
    }
    parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`);
  });
  return parts.join('&');
}

function loadTokens() {
  const rawFromFile = __ENV.TOKENS_FILE ? open(__ENV.TOKENS_FILE) : '';
  const rawFromEnv = __ENV.TOKENS || '';
  const raw = [rawFromFile, rawFromEnv].filter((v) => v).join(',');
  if (!raw) {
    return [];
  }
  return raw
    .split(/[\r\n,]+/)
    .map((t) => t.trim())
    .filter((t) => t.length > 0);
}

const TOKENS_LIST = loadTokens();

function pickToken() {
  if (TOKENS_LIST.length > 0) {
    return TOKENS_LIST[(__VU - 1) % TOKENS_LIST.length];
  }
  if (__ENV.TOKEN) {
    return __ENV.TOKEN;
  }
  return null;
}

function randomString(length) {
  const chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
  let out = '';
  for (let i = 0; i < length; i += 1) {
    out += chars[Math.floor(Math.random() * chars.length)];
  }
  return out;
}

function pseudoUuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

function nextBidPrice(bidState) {
  const seq = bidState.seq;
  bidState.seq += 1;
  const base = BID_PRICE_START !== null ? BID_PRICE_START : BID_PRICE_BASE;
  let candidate;
  if (BID_PRICE_MODE === 'global-seq') {
    const globalIndex = (seq * VU_COUNT) + (__VU - 1);
    candidate = base + (globalIndex * BID_PRICE_STEP);
  } else if (BID_PRICE_START !== null) {
    candidate = base + (seq * BID_PRICE_STEP);
  } else {
    candidate = BID_PRICE_BASE + ((bidState.baseIndex + seq) * BID_PRICE_STEP);
  }
  const minBased = bidState.minBid !== null
    ? bidState.minBid + BID_PRICE_STEP
    : null;
  const price = minBased !== null ? Math.max(candidate, minBased) : candidate;
  return Number(price.toFixed(2));
}

function sendBid(socket, bidState) {
  const bidPrice = nextBidPrice(bidState);
  const payload = JSON.stringify({
    bidPrice,
    quantity: 1,
    idempotencyKey: pseudoUuid(),
  });

  sockjsSend(
    socket,
    stompFrame(
      'SEND',
      {
        destination: `/pub/auction/${AUCTION_ID}/bid`,
        'content-type': 'application/json',
        'content-length': payload.length,
      },
      payload
    )
  );
}

function stepSendEveryMs(startedAt) {
  const elapsed = Date.now() - startedAt;
  return elapsed < STEP_RATE_SWITCH_MS
    ? STEP_FIRST_SEND_EVERY_MS
    : STEP_SECOND_SEND_EVERY_MS;
}

function scheduleStepBids(socket, bidState, startedAt) {
  const intervalMs = stepSendEveryMs(startedAt);
  sendBid(socket, bidState);
  if (intervalMs > 0) {
    socket.setTimeout(() => {
      scheduleStepBids(socket, bidState, startedAt);
    }, intervalMs);
  }
}

function stompFrame(command, headers, body) {
  let frame = `${command}\n`;
  Object.keys(headers || {}).forEach((key) => {
    frame += `${key}:${headers[key]}\n`;
  });
  frame += '\n';
  if (body) {
    frame += body;
  }
  return `${frame}\x00`;
}

function sockjsSend(socket, payload) {
  // WebSocket transport expects a raw JSON array of messages.
  socket.send(JSON.stringify([payload]));
}

function sockjsMessages(data) {
  if (!data) {
    return [];
  }
  try {
    if (data[0] === 'a') {
      return JSON.parse(data.slice(1));
    }
    if (data[0] === '[') {
      return JSON.parse(data);
    }
  } catch (e) {
    return [];
  }
  return [];
}

function stompBody(frame) {
  const marker = '\n\n';
  const idx = frame.indexOf(marker);
  if (idx === -1) {
    return '';
  }
  return frame.slice(idx + marker.length).replace(/\x00$/, '');
}

function updateMinBidFromFrame(frame, bidState) {
  if (!frame.startsWith('MESSAGE')) {
    return;
  }
  const body = stompBody(frame);
  const match = body.match(/최소 입찰가([0-9]+(?:\.[0-9]+)?)/);
  if (!match) {
    return;
  }
  const parsed = parseFloat(match[1]);
  if (!Number.isNaN(parsed)) {
    bidState.minBid = parsed;
  }
}

function buildSockjsUrls(token) {
  const serverId = String(Math.floor(Math.random() * 1000)).padStart(3, '0');
  const sessionId = randomString(8);
  const infoQuery = buildQuery({ token, t: Date.now() });
  const wsQuery = buildQuery({ token });
  const infoUrl = `${HTTP_BASE}/ws-auction/info${infoQuery ? `?${infoQuery}` : ''}`;
  const wsUrl = `${WS_BASE}/ws-auction/${serverId}/${sessionId}/websocket${
    wsQuery ? `?${wsQuery}` : ''
  }`;
  return { infoUrl, wsUrl };
}

export default function () {
  const token = pickToken();
  const useToken = token && token.length > 0;
  const userId = __ENV.USER_ID_BASE
    ? String(parseInt(__ENV.USER_ID_BASE, 10) + __VU - 1)
    : String(__VU);

  const { infoUrl, wsUrl } = buildSockjsUrls(token);
  const infoRes = http.get(infoUrl);
  if (DEBUG && __ITER === 0) {
    console.log(`sockjs info status=${infoRes.status}`);
  }
  if (
    LOG_INFO_SLOW_MS > 0
    && infoRes
    && infoRes.timings
    && infoRes.timings.duration > LOG_INFO_SLOW_MS
  ) {
    console.warn(
      `slow info: status=${infoRes.status} duration_ms=${infoRes.timings.duration}`
    );
  }

  const res = ws.connect(
    wsUrl,
    {
      headers: useToken
        ? {}
        : {
            'X-USER-ID': userId,
            'X-USER-ROLE': 'consumer',
          },
    },
    (socket) => {
      let biddingStarted = false;
      const bidState = {
        baseIndex: (__ITER * VU_COUNT) + (__VU - 1) + 1,
        seq: 0,
        minBid: null,
      };

      if (SESSION_MS > 0) {
        socket.setTimeout(() => {
          socket.close();
        }, SESSION_MS);
      }

      socket.on('message', (data) => {
        if (data === 'o') {
          sockjsSend(
            socket,
            stompFrame('CONNECT', {
              'accept-version': '1.2',
              'heart-beat': '10000,10000',
            })
          );
          return;
        }

        if (data === 'h') {
          return;
        }

        if (data && data[0] === 'c') {
          socket.close();
          return;
        }

        const frames = sockjsMessages(data);
        frames.forEach((frame) => {
          updateMinBidFromFrame(frame, bidState);
          if (frame.startsWith('CONNECTED') && !biddingStarted) {
            biddingStarted = true;

            sockjsSend(
              socket,
              stompFrame('SUBSCRIBE', {
                id: `sub-${userId}`,
                destination: `/topic/auction/${AUCTION_ID}`,
              })
            );

            const startBidding = () => {
              if (USE_STEP_SCENARIO) {
                scheduleStepBids(socket, bidState, Date.now());
                return;
              }

              sendBid(socket, bidState);
              if (SEND_EVERY_MS > 0) {
                socket.setInterval(() => {
                  sendBid(socket, bidState);
                }, SEND_EVERY_MS);
              }
            };

            const startDelayMs = USE_STEP_SCENARIO
              ? STEP_FIRST_SEND_EVERY_MS
              : SEND_DELAY_MS;
            if (startDelayMs > 0) {
              socket.setTimeout(startBidding, startDelayMs);
            } else {
              startBidding();
            }
          }
        });
      });

      socket.on('error', (e) => {
        console.error(`ws error: ${e.error()}`);
      });

      if (SESSION_MS <= 0) {
        socket.setTimeout(() => {
          socket.close();
        }, 5000);
      }
    }
  );

  if (DEBUG && __ITER === 0) {
    console.log(
      `ws.connect status=${res && res.status} error=${res && res.error} code=${res && res.error_code}`
    );
  }
  if (LOG_WS_ERRORS && res && res.status !== 101) {
    console.warn(
      `ws.connect failed status=${res.status} error=${res.error} code=${res.error_code}`
    );
  }

  check(res, {
    'status is 101': (r) => r && r.status === 101,
  });
}
