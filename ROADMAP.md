# ROADMAP.md

Thứ tự triển khai bắt buộc — không nhảy cóc phase. Mỗi phase chỉ "done" khi đạt tiêu chí ở `CLAUDE.md §7`.

- [ ] **Phase 1 — Skeleton**: tạo monorepo, `common`, `backend`, `simulator`, `frontend`, `docker-compose.yml` (infra only). Chưa xử lý realtime.
- [ ] **Phase 2 — Common DTO/Enum**: `TargetClassification`, `TrajectoryType`, `PositionDto`, `TargetUpdateEvent`, `TargetUpdateBatchEvent`, `TargetIdUtil`.
- [ ] **Phase 3 — Simulator**: `TargetFactory`, 3 `TrajectoryCalculator` (circle/straight/polyline), `SimulationScheduler` (300ms), publish Kafka. Test tăng dần: 100 → 1.000 → 10.000 target.
- [ ] **Phase 4 — Backend consume Kafka**: `TargetUpdateConsumer` + `TargetIngestionService`, chỉ log số lượng nhận được mỗi batch (chưa cần DB).
- [ ] **Phase 5 — Redis current state**: batch/pipeline write, API `GET /api/targets`, `GET /api/targets/{id}`.
- [ ] **Phase 6 — WebSocket realtime**: broadcast batch tới frontend.
- [ ] **Phase 7 — Frontend realtime**: `FlightMap`, `targetSocket`, `useTargetStore`, `TargetLayer` (deck.gl). Test tăng dần 100 → 10.000 điểm.
- [ ] **Phase 8 — ClickHouse history**: bulk insert theo batch, TTL 7 ngày, API history.
- [ ] **Phase 9 — Frontend history**: click target → gọi API history → vẽ trail.
- [ ] **Phase 10 — Tối ưu**: giảm payload WebSocket (nén/binary), theo dõi Kafka lag / Redis memory / ClickHouse insert rate, đảm bảo chu kỳ ổn định ≤300ms ở 10.000 target liên tục.

## Nghiệm thu cuối (bắt buộc trước khi coi hệ thống hoàn thành)

- [ ] Chạy đồng thời ≥ 10.000 mục tiêu, không crash, không leak sau 30 phút.
- [ ] Đo được chu kỳ cập nhật end-to-end ≤ 300ms.
- [ ] Dữ liệu lịch sử truy vấn được ít nhất 7 ngày (hoặc verify TTL config đúng, vì test thực tế 7 ngày liên tục không khả thi trong dev — verify bằng cách insert dữ liệu giả với timestamp cũ và kiểm tra TTL/query).
- [ ] Frontend hiển thị mượt 10.000 điểm + filter + search + trail lịch sử.
