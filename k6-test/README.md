# k6 WebSocket STOMP 부하 테스트 (SockJS)

이 폴더에는 다음 파일이 있습니다:
- `ws_test.js`: SockJS + STOMP 입찰 부하 테스트 스크립트
- `tokens.txt`: 게이트웨이 테스트용 JWT (1줄 1개)
- `test_command.txt`: PowerShell 실행 커맨드 모음
- `outbox_load.sql`: outbox 더미 데이터 부하용 SQL

## 동작 방식
- SockJS info 엔드포인트 호출: `GET /ws-auction/info`
- WebSocket 연결: `/ws-auction/{server}/{session}/websocket`
- STOMP `CONNECT` 후 `SUBSCRIBE /topic/auction/{auctionId}`
- 세션 동안 `SEND /pub/auction/{auctionId}/bid` 반복 전송
- 서버의 "최소 입찰가" 메시지에 맞춰 입찰가 자동 증가 (`updateMinBidFromFrame` 참고)

## 실행 전 확인
- Docker 또는 k6 실행 환경이 필요합니다 (예시는 Docker 사용)
- 외부 WS를 사용할 경우 `WS_BASE`, `HTTP_BASE`를 외부 주소로 지정하세요
- 토큰 사용 시 `TOKENS_FILE` 또는 `TOKEN`을 지정하세요

## 환경 변수
- `WS_BASE`: WebSocket base URL (기본값: `ws://host.docker.internal:6600`)
- `HTTP_BASE`: HTTP base URL (기본값: `WS_BASE`에서 `ws` -> `http` 변환)
- `AUCTION_ID`: auction UUID
- `TOKENS_FILE`: 컨테이너 내 토큰 파일 경로 (1줄 1 JWT)
- `TOKEN`: 단일 JWT (`TOKENS_FILE` 미지정 시 사용)
- `SEND_DELAY_MS`: 연결 후 첫 입찰 딜레이 (기본값: `0`)
- `SEND_EVERY_MS`: 입찰 전송 간격 (기본값: `1000`)
- `SESSION_MS`: 세션 유지 시간 (기본값: `10000`)
- `BID_PRICE_BASE`: 기본 입찰가 (기본값: `1000`)
- `BID_PRICE_STEP`: 입찰가 증가 폭 (기본값: `0.01`)
- `BID_PRICE_START`: 시작 입찰가 (설정 시 VU별 메시지마다 `BID_PRICE_STEP`씩 증가)
- `BID_PRICE_MODE`: `global-seq` 사용 시 VU 간 중복 없이 증가 (정확한 전역 순서 보장은 아님)
- `VUS`, `DURATION`: 일반 시나리오 옵션 (`--vus`, `--duration`로도 지정 가능)
- `USER_ID_BASE`: 직접 모드(토큰 없음)에서 사용할 시작 user id
- `DEBUG=1`: SockJS info/handshake 로그 출력
- `LOG_WS_ERRORS=1`: WebSocket 핸드셰이크 실패 로그 출력
- `LOG_INFO_SLOW_MS`: `/ws-auction/info` 응답이 지정 ms 이상이면 로그 출력
- `SCENARIO=step-bid`: 단계 시나리오 사용 (아래 참고)
- `STEP_FIRST_RPS`: 단계 시나리오 1차 전송률 (VU당 초당 메시지 수)
- `STEP_SECOND_RPS`: 단계 시나리오 2차 전송률 (VU당 초당 메시지 수)
- `STEP_RATE_SWITCH_MS`: 1차 -> 2차 전송률 전환 시점 (밀리초)

## 단계 시나리오 (step-bid)
- VU 단계: 50 → 100 → 150 → 200 → 250 → 300 → 350 → 400 (각 단계 2분)
- 각 단계는 1분 동안 `STEP_FIRST_RPS`, 이후 1분 동안 `STEP_SECOND_RPS`로 전송
- `SCENARIO=step-bid` 사용 시 `VUS`, `DURATION`은 무시됩니다

요청한 패턴 예시:
- 50 VU 2분 (1분: VU당 5 msg/s, 다음 1분: VU당 10 msg/s)
- 100 VU 2분 동일 패턴
- 150 VU 2분 동일 패턴
- 200 VU 2분 동일 패턴
- 250 VU 2분 동일 패턴
- 300 VU 2분 동일 패턴
- 350 VU 2분 동일 패턴
- 400 VU 2분 동일 패턴

## 최근 사용 시나리오 요약
- GW 경유 (`host.docker.internal:17700`)
- 토큰 파일 사용 (`TOKENS_FILE=/scripts/tokens.txt`)
- 입찰가: `BID_PRICE_START=1001`, `BID_PRICE_STEP=1`
- 단계 시나리오: 50→400 VU, 각 2분 (1분 5 msg/s → 1분 10 msg/s)

## Commands (PowerShell)

GW 경유 + 단계 시나리오 (50→400 VU, 각 2분):
```
docker run --rm -i -p 5665:5665 -v "${PWD}\k6-test:/scripts" `
  -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_HOST=0.0.0.0 `
  -e SCENARIO=step-bid `
  -e STEP_FIRST_RPS=5 -e STEP_SECOND_RPS=10 -e STEP_RATE_SWITCH_MS=60000 `
  -e WS_BASE=ws://host.docker.internal:17700 `
  -e HTTP_BASE=http://host.docker.internal:17700 `
  -e TOKENS_FILE=/scripts/tokens.txt `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  -e BID_PRICE_START=1001 -e BID_PRICE_STEP=1 `
  grafana/k6 run --quiet /scripts/ws_test.js
```

Gateway, 다중 토큰, 지속 입찰 (500 VU, 60s, 대시보드):
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

Gateway, 다중 토큰, 지속 입찰 (1000 VU, 5m, 대시보드):
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

Direct auction service (토큰 없음, 헤더 기반):
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
  -e SCENARIO=step-bid `
  -e STEP_FIRST_RPS=5 -e STEP_SECOND_RPS=10 -e STEP_RATE_SWITCH_MS=60000 `
  -e WS_BASE=ws://host.docker.internal:17700 `
  -e HTTP_BASE=http://host.docker.internal:17700 `
  -e TOKENS_FILE=/scripts/tokens.txt `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  -e BID_PRICE_START=1001 -e BID_PRICE_STEP=1 `
  grafana/k6 run --quiet /scripts/ws_test.js 2>&1 | Tee-Object -FilePath k6-test\k6_run.log
```

## Redis 검증
```
docker exec redis-stack redis-cli HGET auction:11111111-1111-1111-1111-111111111111:open minPrice
docker exec redis-stack redis-cli HGET auction:11111111-1111-1111-1111-111111111111:open stock
docker exec redis-stack redis-cli ZCARD auction:11111111-1111-1111-1111-111111111111:bids
docker exec redis-stack redis-cli ZREVRANGE auction:11111111-1111-1111-1111-111111111111:bids 0 9 WITHSCORES
```

## Codex로 다시 실행하기
다음에 같은 테스트를 Codex에 요청할 때는 이런 식으로 말하면 됩니다:
- "k6-test step-bid 시나리오로 50→400 VU, 각 2분, VU당 1초에 5회 → 10회로 전송. GW는 host.docker.internal:17700, 토큰 파일 사용, BID_PRICE_START=1001, BID_PRICE_STEP=1로 PowerShell 커맨드 만들어줘"

## Notes
- 입찰 수가 `stock`에 도달하면 컷오프 이하 입찰이 거절됩니다. 스크립트는 최소 입찰가에 맞춰 자동으로 올립니다.
- 높은 VU에서 `/ws-auction/info`의 `connection refused`나 WebSocket 프로토콜 오류가 날 수 있습니다. VU 램프업 또는 게이트웨이 용량 조정이 필요할 수 있습니다.
- SockJS URL에 랜덤 세션 ID가 포함되어 k6 메트릭 카디널리티가 높아질 수 있습니다.
