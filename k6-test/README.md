# k6 WebSocket STOMP load test (SockJS)

This folder contains:
- `ws_test.js`: SockJS + STOMP load test for auction bidding
- `tokens.txt`: one JWT per line for gateway tests
- `test_command.txt`: copy/paste commands

## How the script works
- Calls SockJS info endpoint: `GET /ws-auction/info`
- Opens WebSocket: `/ws-auction/{server}/{session}/websocket`
- Sends STOMP `CONNECT`, then `SUBSCRIBE /topic/auction/{auctionId}`
- Sends repeated `SEND /pub/auction/{auctionId}/bid` while the session is open
- Adjusts bid price upward based on the server "min bid" message (see `updateMinBidFromFrame` in `ws_test.js`)

## Environment variables
- `WS_BASE`: WebSocket base URL (default: `ws://host.docker.internal:6600`)
- `HTTP_BASE`: HTTP base URL (default: `WS_BASE` with `ws` -> `http`)
- `AUCTION_ID`: auction UUID
- `TOKENS_FILE`: path to tokens file in container (one JWT per line)
- `TOKEN`: single JWT (used if `TOKENS_FILE` is not set)
- `SEND_DELAY_MS`: delay before first bid after connect (default: `0`)
- `SEND_EVERY_MS`: interval between bids per session (default: `1000`)
- `SESSION_MS`: session lifetime before closing (default: `10000`)
- `BID_PRICE_BASE`: base bid price (default: `1000`)
- `BID_PRICE_STEP`: bid price increment (default: `0.01`)
- `VUS`, `DURATION`: k6 options (also set via `--vus`, `--duration`)
- `USER_ID_BASE`: starting user id for direct mode (no token)
- `DEBUG=1`: prints SockJS info/handshake status

## Commands (PowerShell)

Gateway, multi-token, continuous bidding (500 VU, 60s, dashboard):
```
docker run --rm -i -p 5665:5665 -v "${PWD}\k6-test:/scripts" `
  -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_HOST=0.0.0.0 `
  grafana/k6 run --vus 500 --duration 60s `
  -e WS_BASE=ws://host.docker.internal:17700 `
  -e HTTP_BASE=http://host.docker.internal:17700 `
  -e TOKENS_FILE=/scripts/tokens.txt `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  -e SEND_DELAY_MS=100 -e SEND_EVERY_MS=1000 -e SESSION_MS=60000 `
  -e BID_PRICE_BASE=1000 -e BID_PRICE_STEP=0.01 `
  /scripts/ws_test.js
```

Gateway, multi-token, continuous bidding (1000 VU, 5m, dashboard):
```
docker run --rm -i -p 5665:5665 -v "${PWD}\k6-test:/scripts" `
  -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_HOST=0.0.0.0 `
  grafana/k6 run --vus 1000 --duration 300s `
  -e WS_BASE=ws://host.docker.internal:17700 `
  -e HTTP_BASE=http://host.docker.internal:17700 `
  -e TOKENS_FILE=/scripts/tokens.txt `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  -e SEND_DELAY_MS=100 -e SEND_EVERY_MS=1000 -e SESSION_MS=300000 `
  -e BID_PRICE_BASE=1000 -e BID_PRICE_STEP=0.01 `
  /scripts/ws_test.js
```

Direct auction service (no token, header-based):
```
docker run --rm -i -v "${PWD}\k6-test:/scripts" grafana/k6 run `
  --vus 500 --duration 60s `
  -e WS_BASE=ws://host.docker.internal:6600 `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  -e SEND_DELAY_MS=100 -e SEND_EVERY_MS=1000 -e SESSION_MS=60000 `
  -e BID_PRICE_BASE=1000 -e BID_PRICE_STEP=0.01 `
  /scripts/ws_test.js
```

Dashboard URL: `http://localhost:5665`

## Log capture
```
docker run --rm -i -p 5665:5665 -v "${PWD}\k6-test:/scripts" `
  -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_HOST=0.0.0.0 `
  grafana/k6 run --vus 500 --duration 60s `
  -e WS_BASE=ws://host.docker.internal:17700 `
  -e HTTP_BASE=http://host.docker.internal:17700 `
  -e TOKENS_FILE=/scripts/tokens.txt `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  -e SEND_DELAY_MS=100 -e SEND_EVERY_MS=1000 -e SESSION_MS=60000 `
  -e BID_PRICE_BASE=1000 -e BID_PRICE_STEP=0.01 `
  /scripts/ws_test.js 2>&1 | Tee-Object -FilePath k6-test\k6_run.log
```

## Redis verification
```
docker exec redis-stack redis-cli HGET auction:11111111-1111-1111-1111-111111111111:open minPrice
docker exec redis-stack redis-cli HGET auction:11111111-1111-1111-1111-111111111111:open stock
docker exec redis-stack redis-cli ZCARD auction:11111111-1111-1111-1111-111111111111:bids
docker exec redis-stack redis-cli ZREVRANGE auction:11111111-1111-1111-1111-111111111111:bids 0 9 WITHSCORES
```

## Notes
- After bid count reaches `stock`, bids below the cutoff are rejected. The script now increases bid price automatically.
- High VU counts can produce `connection refused` on `/ws-auction/info` or WebSocket protocol errors. Consider ramping VUs or increasing gateway capacity.
- SockJS URLs include random session IDs, so k6 may warn about high metric cardinality.
