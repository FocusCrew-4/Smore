# k6 WebSocket STOMP 부하 테스트 (SockJS)

이 폴더에는 다음 파일이 있습니다:
- `ws_test.js`: SockJS + STOMP 입찰 부하 테스트 스크립트
- `tokens.txt`: 게이트웨이 테스트용 JWT (1줄 1개)
- `test_command.txt`: PowerShell 실행 커맨드 모음
- `outbox_load.sql`: outbox 더미 데이터 부하용 SQL

## 동작 방식
- SockJS info 엔드포인트 호출: `GET /ws-auction/info` (`WS_MODE=sockjs`)
- WebSocket 연결: `/ws-auction/{server}/{session}/websocket` (`WS_MODE=sockjs`)
- 직접 WebSocket 연결: `/ws-auction` (`WS_MODE=ws`)
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
- `WS_MODE`: `sockjs`(기본) 또는 `ws` (직접 WebSocket, `/info` 미사용)
- `SKIP_INFO=1`: SockJS `/info` 호출 생략 (게이트웨이에서 `/info` 미지원 시 사용)
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
- `SCENARIO=rate-step`: 고정 VU + 전송률 단계 시나리오 (아래 참고)
- `STEP_FIRST_RPS`: 단계 시나리오 1차 전송률 (VU당 초당 메시지 수)
- `STEP_SECOND_RPS`: 단계 시나리오 2차 전송률 (VU당 초당 메시지 수)
- `STEP_RATE_SWITCH_MS`: 1차 -> 2차 전송률 전환 시점 (밀리초)
- `RATE_STEP_RPS_LIST`: rate-step 전송률 목록 (기본값: `5,10,15,20,25`)
- `RATE_STEP_MINUTES`: rate-step 단계당 분 (기본값: `1`)
- `RATE_STEP_VUS`: rate-step 고정 VU (기본값: `VUS`)

## 단계 시나리오 (step-bid)
- VU 단계: 550 → 600 → 650 → 700 → 750 (각 단계 1분)
- 각 단계는 30초 동안 `STEP_FIRST_RPS`, 이후 30초 동안 `STEP_SECOND_RPS`로 전송
- `SCENARIO=step-bid` 사용 시 `VUS`, `DURATION`은 무시됩니다

요청한 패턴 예시:
- 550 VU 1분 (30초: VU당 5 msg/s, 다음 30초: VU당 10 msg/s)
- 600 VU 1분 동일 패턴
- 650 VU 1분 동일 패턴
- 700 VU 1분 동일 패턴
- 750 VU 1분 동일 패턴

## 고정 VU + 전송률 단계 (rate-step)
- 고정 VU로 여러 전송률을 순차 테스트할 때 사용합니다.
- `SCENARIO=rate-step` 사용 시 `VUS`, `DURATION`은 무시됩니다.
- 예: VU 300, 5/10/15/20/25 msg/s (각 1분)

요청한 패턴 예시:
- `RATE_STEP_VUS=300`, `RATE_STEP_RPS_LIST=5,10,15,20,25`, `RATE_STEP_MINUTES=1`

## Metrics
- `bid_sent`: k6 Counter (전송 시도 수). `rate` 값이 VU 기준 실제 전송 msg/s 입니다.
- `ws_connect_ok`: k6 Rate (WebSocket 연결 성공률). summary에 `ws_connect_ok rate`로 출력됩니다.

## 최근 사용 시나리오 요약
- GW 경유 (`host.docker.internal:17700`)
- 토큰 파일 사용 (`TOKENS_FILE=/scripts/tokens.txt`)
- 입찰가: `BID_PRICE_START=1001`, `BID_PRICE_STEP=1`
- 단계 시나리오: 550→750 VU, 각 1분 (30초 5 msg/s → 30초 10 msg/s)
- 고정 VU: 300, 5 msg/s, 4분 (`SEND_EVERY_MS=200`, `DURATION=4m`)

## 테스트 기준/방법 (최근)
- 경매가 테스트 시간 동안 유지되어야 합니다 (중간 종료 시 결과 무효).
- 고정 VU + 현실적인 전송률(예: VU 300, 5 msg/s)을 기준으로 내부/외부 브로커를 동일 조건으로 비교합니다.
- 결과는 `k6_summary.json`, `k6_summary.txt`, `k6_dashboard.html`로 보관합니다.
- 대시보드가 깨질 수 있으니 HTML export를 기준으로 기록합니다.

## Commands (PowerShell)

GW 경유 + 단계 시나리오 (550→750 VU, 각 1분):
```
docker run --rm -i -p 5665:5665 -v "${PWD}\k6-test:/scripts" `
  -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_HOST=0.0.0.0 `
  -e K6_WEB_DASHBOARD_PERIOD=10s -e K6_WEB_DASHBOARD_EXPORT=/scripts/k6_dashboard.html `
  -e SCENARIO=step-bid `
  -e STEP_FIRST_RPS=5 -e STEP_SECOND_RPS=10 -e STEP_RATE_SWITCH_MS=30000 `
  -e WS_BASE=ws://host.docker.internal:17700 `
  -e HTTP_BASE=http://host.docker.internal:17700 `
  -e SKIP_INFO=1 `
  -e TOKENS_FILE=/scripts/tokens.txt `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  -e BID_PRICE_START=1001 -e BID_PRICE_STEP=1 `
  grafana/k6 run /scripts/ws_test.js
```

GW 경유, 고정 VU 300 + 5 msg/s, 4분:
```
docker run --rm -i -p 5665:5665 -v "${PWD}\k6-test:/scripts" `
  -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_HOST=0.0.0.0 `
  -e K6_WEB_DASHBOARD_PERIOD=10s -e K6_WEB_DASHBOARD_EXPORT=/scripts/k6_dashboard.html `
  -e VUS=300 -e DURATION=4m -e SESSION_MS=240000 `
  -e SEND_EVERY_MS=200 `
  -e WS_BASE=ws://host.docker.internal:17700 `
  -e HTTP_BASE=http://host.docker.internal:17700 `
  -e SKIP_INFO=1 `
  -e TOKENS_FILE=/scripts/tokens.txt `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  grafana/k6 run /scripts/ws_test.js
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
  -e K6_WEB_DASHBOARD_PERIOD=10s -e K6_WEB_DASHBOARD_EXPORT=/scripts/k6_dashboard.html `
  -e SCENARIO=step-bid `
  -e STEP_FIRST_RPS=5 -e STEP_SECOND_RPS=10 -e STEP_RATE_SWITCH_MS=30000 `
  -e WS_BASE=ws://host.docker.internal:17700 `
  -e HTTP_BASE=http://host.docker.internal:17700 `
  -e SKIP_INFO=1 `
  -e TOKENS_FILE=/scripts/tokens.txt `
  -e AUCTION_ID=11111111-1111-1111-1111-111111111111 `
  -e BID_PRICE_START=1001 -e BID_PRICE_STEP=1 `
  grafana/k6 run /scripts/ws_test.js 2>&1 | Tee-Object -FilePath k6-test\k6_run.log
```

## Redis 처리량 측정
PowerShell 예시:
```
.\k6-test\redis_measure.ps1 -AuctionId 11111111-1111-1111-1111-111111111111 -Seconds 60 -RedisContainer redis-stack
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
- "k6-test step-bid 시나리오로 550→750 VU, 각 1분, VU당 1초에 5회 → 10회로 전송. GW는 host.docker.internal:17700, 토큰 파일 사용, BID_PRICE_START=1001, BID_PRICE_STEP=1로 PowerShell 커맨드 만들어줘"

## Notes
- 입찰 수가 `stock`에 도달하면 컷오프 이하 입찰이 거절됩니다. 스크립트는 최소 입찰가에 맞춰 자동으로 올립니다.
- 높은 VU에서 `/ws-auction/info`의 `connection refused`나 WebSocket 프로토콜 오류가 날 수 있습니다. VU 램프업 또는 게이트웨이 용량 조정이 필요할 수 있습니다.
- SockJS URL에 랜덤 세션 ID가 포함되어 k6 메트릭 카디널리티가 높아질 수 있습니다.
- k6 대시보드 안정화를 위해 `systemTags`를 최소화했습니다.
- WebSocket 부하 시 대시보드 렌더링이 깨질 수 있으니 `k6_dashboard.html`을 기준으로 확인하세요.
- 실행이 끝나면 `k6_summary.json`, `k6_summary.txt`가 자동 생성됩니다.
