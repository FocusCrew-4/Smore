import http from 'k6/http';
import ws from 'k6/ws';
import { check } from 'k6';
import { Counter, Rate } from 'k6/metrics';

const ENV = __ENV;

const USE_STEP_SCENARIO = ENV.SCENARIO === 'step-bid';
const USE_RATE_STEP_SCENARIO = ENV.SCENARIO === 'rate-step';
const STEP_STAGE_MINUTES = 1;
const STEP_RATE_SWITCH_MS = ENV.STEP_RATE_SWITCH_MS
  ? parseInt(ENV.STEP_RATE_SWITCH_MS, 10)
  : 30000;
const STEP_FIRST_RPS = ENV.STEP_FIRST_RPS ? parseFloat(ENV.STEP_FIRST_RPS) : 5;
const STEP_SECOND_RPS = ENV.STEP_SECOND_RPS ? parseFloat(ENV.STEP_SECOND_RPS) : 10;
const STEP_FIRST_SEND_EVERY_MS = rateToInterval(STEP_FIRST_RPS);
const STEP_SECOND_SEND_EVERY_MS = rateToInterval(STEP_SECOND_RPS);
const STEP_SCENARIO_VUS = [550, 600, 650, 700, 750];

const DEFAULT_VUS = ENV.VUS ? parseInt(ENV.VUS, 10) : 500;
const DEFAULT_DURATION = ENV.DURATION || '30s';
const RATE_STEP_MINUTES = ENV.RATE_STEP_MINUTES
  ? parseInt(ENV.RATE_STEP_MINUTES, 10)
  : 1;
const RATE_STEP_RPS_LIST = parseRateList(
  ENV.RATE_STEP_RPS_LIST || '5,10,15,20,25'
);
const RATE_STEP_VUS = ENV.RATE_STEP_VUS
  ? parseInt(ENV.RATE_STEP_VUS, 10)
  : DEFAULT_VUS;

export const options = {
  systemTags: ['scenario'],
  ...(USE_STEP_SCENARIO
    ? { scenarios: buildStepScenarios() }
    : USE_RATE_STEP_SCENARIO
      ? { scenarios: buildRateScenarios() }
      : { vus: DEFAULT_VUS, duration: DEFAULT_DURATION }),
};

const WS_BASE = ENV.WS_BASE || 'ws://host.docker.internal:6600';
const HTTP_BASE = ENV.HTTP_BASE || WS_BASE.replace(/^ws/i, 'http');
const AUCTION_ID = ENV.AUCTION_ID || '11111111-1111-1111-1111-111111111111';

const USE_SOCKJS = (ENV.WS_MODE || 'sockjs').toLowerCase() !== 'ws';
const SKIP_INFO = ENV.SKIP_INFO === '1';

const DEBUG = ENV.DEBUG === '1';
const LOG_WS_ERRORS = ENV.LOG_WS_ERRORS === '1';
const LOG_INFO_SLOW_MS = ENV.LOG_INFO_SLOW_MS ? parseInt(ENV.LOG_INFO_SLOW_MS, 10) : 0;

const SEND_DELAY_MS = ENV.SEND_DELAY_MS ? parseInt(ENV.SEND_DELAY_MS, 10) : 0;
const SEND_EVERY_MS = ENV.SEND_EVERY_MS ? parseInt(ENV.SEND_EVERY_MS, 10) : 1000;
const SESSION_MS = ENV.SESSION_MS ? parseInt(ENV.SESSION_MS, 10) : 10000;

const BID_PRICE_BASE = ENV.BID_PRICE_BASE ? parseFloat(ENV.BID_PRICE_BASE) : 1000;
const BID_PRICE_STEP = ENV.BID_PRICE_STEP ? parseFloat(ENV.BID_PRICE_STEP) : 0.01;
const BID_PRICE_START = ENV.BID_PRICE_START ? parseFloat(ENV.BID_PRICE_START) : null;
const BID_PRICE_MODE = (ENV.BID_PRICE_MODE || '').toLowerCase();
const VU_COUNT = ENV.SCENARIO_VUS ? parseInt(ENV.SCENARIO_VUS, 10) : DEFAULT_VUS;

const bidSent = new Counter('bid_sent');
const wsConnectOk = new Rate('ws_connect_ok');

function rateToInterval(rps) {
  if (!rps || rps <= 0) {
    return 0;
  }
  return Math.max(1, Math.round(1000 / rps));
}

function parseRateList(raw) {
  if (!raw) {
    return [];
  }
  return String(raw)
    .split(/[\s,]+/)
    .map((value) => parseFloat(value))
    .filter((value) => Number.isFinite(value) && value > 0);
}

function buildStepScenarios() {
  const scenarios = {};
  const stageSessionMs = STEP_STAGE_MINUTES * 60 * 1000;
  STEP_SCENARIO_VUS.forEach((vus, index) => {
    scenarios[`step_${vus}vus`] = {
      executor: 'constant-vus',
      vus,
      duration: `${STEP_STAGE_MINUTES}m`,
      startTime: `${index * STEP_STAGE_MINUTES}m`,
      gracefulStop: '0s',
      env: {
        SESSION_MS: String(stageSessionMs),
        SCENARIO_VUS: String(vus),
      },
    };
  });
  return scenarios;
}

function buildRateScenarios() {
  const scenarios = {};
  const stageSessionMs = RATE_STEP_MINUTES * 60 * 1000;
  RATE_STEP_RPS_LIST.forEach((rps, index) => {
    const intervalMs = rateToInterval(rps);
    scenarios[`rate_${rps}rps`] = {
      executor: 'constant-vus',
      vus: RATE_STEP_VUS,
      duration: `${RATE_STEP_MINUTES}m`,
      startTime: `${index * RATE_STEP_MINUTES}m`,
      gracefulStop: '0s',
      env: {
        SEND_EVERY_MS: String(intervalMs),
        SCENARIO_VUS: String(RATE_STEP_VUS),
        SESSION_MS: String(stageSessionMs),
      },
    };
  });
  return scenarios;
}

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
  const rawFromFile = ENV.TOKENS_FILE ? open(ENV.TOKENS_FILE) : '';
  const rawFromEnv = ENV.TOKENS || '';
  const raw = [rawFromFile, rawFromEnv].filter((value) => value).join(',');
  if (!raw) {
    return [];
  }
  return raw
    .split(/[\r\n,]+/)
    .map((token) => token.trim())
    .filter((token) => token.length > 0);
}

const TOKENS = loadTokens();

function pickToken() {
  if (TOKENS.length > 0) {
    return TOKENS[(__VU - 1) % TOKENS.length];
  }
  if (ENV.TOKEN) {
    return ENV.TOKEN;
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

function sendFrame(socket, frame) {
  if (USE_SOCKJS) {
    socket.send(JSON.stringify([frame]));
    return;
  }
  socket.send(frame);
}

function sendBid(socket, bidState) {
  const bidPrice = nextBidPrice(bidState);
  const payload = JSON.stringify({
    bidPrice,
    quantity: 1,
    idempotencyKey: pseudoUuid(),
  });

  sendFrame(
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
  bidSent.add(1);
}

function stepIntervalMs(startedAt) {
  const elapsed = Date.now() - startedAt;
  return elapsed < STEP_RATE_SWITCH_MS
    ? STEP_FIRST_SEND_EVERY_MS
    : STEP_SECOND_SEND_EVERY_MS;
}

function scheduleStepBids(socket, bidState, startedAt) {
  const intervalMs = stepIntervalMs(startedAt);
  sendBid(socket, bidState);
  if (intervalMs > 0) {
    socket.setTimeout(() => {
      scheduleStepBids(socket, bidState, startedAt);
    }, intervalMs);
  }
}

function parseSockjsMessages(data) {
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

function parseStompFrames(data) {
  if (!data) {
    return [];
  }
  return String(data)
    .split('\x00')
    .filter((frame) => frame.length > 0);
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

function buildWsUrls(token) {
  if (!USE_SOCKJS) {
    const wsQuery = buildQuery({ token });
    const wsUrl = `${WS_BASE}/ws-auction${wsQuery ? `?${wsQuery}` : ''}`;
    return { infoUrl: null, wsUrl };
  }

  const serverId = String(Math.floor(Math.random() * 1000)).padStart(3, '0');
  const sessionId = randomString(8);
  const infoQuery = buildQuery({ token, t: Date.now() });
  const wsQuery = buildQuery({ token });
  const infoUrl = SKIP_INFO
    ? null
    : `${HTTP_BASE}/ws-auction/info${infoQuery ? `?${infoQuery}` : ''}`;
  const wsUrl = `${WS_BASE}/ws-auction/${serverId}/${sessionId}/websocket${
    wsQuery ? `?${wsQuery}` : ''
  }`;
  return { infoUrl, wsUrl };
}

function startBidding(socket, bidState) {
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
}

function summaryValue(data, metricName, key) {
  if (!data.metrics || !data.metrics[metricName]) {
    return null;
  }
  const values = data.metrics[metricName].values || {};
  return Object.prototype.hasOwnProperty.call(values, key) ? values[key] : null;
}

export function handleSummary(data) {
  const checksRate = summaryValue(data, 'checks', 'rate');
  const checksPass = summaryValue(data, 'checks', 'passes');
  const checksFail = summaryValue(data, 'checks', 'fails');
  const bidSentRate = summaryValue(data, 'bid_sent', 'rate');
  const bidSentCount = summaryValue(data, 'bid_sent', 'count');
  const wsSentRate = summaryValue(data, 'ws_msgs_sent', 'rate');
  const wsRecvRate = summaryValue(data, 'ws_msgs_received', 'rate');
  const wsConnectP95 = summaryValue(data, 'ws_connecting', 'p(95)');
  const wsConnectOkRate = summaryValue(data, 'ws_connect_ok', 'rate');
  const vusMax = summaryValue(data, 'vus_max', 'max');

  const lines = [];
  lines.push('k6 summary');
  if (checksRate !== null) {
    lines.push(`checks rate: ${checksRate}`);
  }
  if (checksPass !== null || checksFail !== null) {
    lines.push(`checks pass/fail: ${checksPass || 0}/${checksFail || 0}`);
  }
  if (bidSentRate !== null || bidSentCount !== null) {
    lines.push(`bid_sent rate/count: ${bidSentRate || 0}/${bidSentCount || 0}`);
  }
  if (wsSentRate !== null) {
    lines.push(`ws_msgs_sent rate: ${wsSentRate}`);
  }
  if (wsRecvRate !== null) {
    lines.push(`ws_msgs_received rate: ${wsRecvRate}`);
  }
  if (wsConnectP95 !== null) {
    lines.push(`ws_connecting p95: ${wsConnectP95}`);
  }
  if (wsConnectOkRate !== null) {
    lines.push(`ws_connect_ok rate: ${wsConnectOkRate}`);
  }
  if (vusMax !== null) {
    lines.push(`vus_max: ${vusMax}`);
  }
  const summaryText = `${lines.join('\n')}\n`;

  return {
    '/scripts/k6_summary.json': JSON.stringify(data, null, 2),
    '/scripts/k6_summary.txt': summaryText,
    stdout: summaryText,
  };
}

export default function () {
  const token = pickToken();
  const useToken = token && token.length > 0;
  const userId = ENV.USER_ID_BASE
    ? String(parseInt(ENV.USER_ID_BASE, 10) + __VU - 1)
    : String(__VU);

  const { infoUrl, wsUrl } = buildWsUrls(token);
  if (USE_SOCKJS && !SKIP_INFO) {
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
  } else if (DEBUG && __ITER === 0) {
    console.log('sockjs info skipped');
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

      const connectStomp = () => {
        sendFrame(
          socket,
          stompFrame('CONNECT', {
            'accept-version': '1.2',
            'heart-beat': '10000,10000',
          })
        );
      };

      if (SESSION_MS > 0) {
        socket.setTimeout(() => {
          socket.close();
        }, SESSION_MS);
      }

      if (!USE_SOCKJS) {
        connectStomp();
      }

      const handleFrames = (frames) => {
        frames.forEach((frame) => {
          updateMinBidFromFrame(frame, bidState);
          if (frame.startsWith('CONNECTED') && !biddingStarted) {
            biddingStarted = true;

            sendFrame(
              socket,
              stompFrame('SUBSCRIBE', {
                id: `sub-${userId}`,
                destination: `/topic/auction/${AUCTION_ID}`,
              })
            );

            const startDelayMs = USE_STEP_SCENARIO
              ? STEP_FIRST_SEND_EVERY_MS
              : SEND_DELAY_MS;
            if (startDelayMs > 0) {
              socket.setTimeout(() => startBidding(socket, bidState), startDelayMs);
            } else {
              startBidding(socket, bidState);
            }
          }
        });
      };

      socket.on('message', (data) => {
        if (USE_SOCKJS) {
          if (data === 'o') {
            connectStomp();
            return;
          }
          if (data === 'h') {
            return;
          }
          if (data && data[0] === 'c') {
            socket.close();
            return;
          }
          handleFrames(parseSockjsMessages(data));
          return;
        }

        handleFrames(parseStompFrames(data));
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

  wsConnectOk.add(res && res.status === 101);
  check(res, {
    'status is 101': (r) => r && r.status === 101,
  });
}
