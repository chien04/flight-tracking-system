# ARCHITECTURE.md

Chi tiết kỹ thuật cho hệ thống Flight Tracking Realtime. Đọc cùng `CLAUDE.md`.

## 1. Luồng dữ liệu

```
Simulator: mỗi 300ms tính lại vị trí 10.000 target
    → gom thành 1 TargetUpdateBatchEvent
    → publish vào Kafka topic "flight-target-updates"

Backend: TargetUpdateConsumer consume batch
    → TargetIngestionService điều phối:
        1. TargetCurrentStateService  → ghi Redis (pipeline/hash batch)
        2. TargetHistoryService       → bulk insert ClickHouse
        3. TargetRealtimePushService  → broadcast WebSocket batch

Frontend: nhận WebSocket batch
    → convert thành Map<targetId, TargetCurrent>
    → update Zustand store 1 lần/batch
    → deck.gl ScatterplotLayer re-render toàn bộ layer từ store
```

## 2. Kafka

- **Topic**: `flight-target-updates`
- **1 producer** (simulator) → **1 consumer group** (backend). Lý do dùng Kafka thay vì gọi HTTP trực tiếp: decouple simulator/backend, có buffer khi backend chậm, có thể replay, dễ thêm consumer khác (vd: pipeline analytics) sau này mà không đụng simulator.
- Message format (JSON, có thể đổi sang Avro/Protobuf nếu cần giảm băng thông sau):

```json
{
  "timestamp": 1783072800300,
  "targets": [
    {
      "targetId": "0001",
      "latitude": 21.0285,
      "longitude": 105.8542,
      "altitude": 1200,
      "classification": "FRIEND",
      "timestamp": 1783072800300
    }
  ]
}
```

- Rule: 1 batch = tối đa 10.000 target. Không publish message riêng cho từng target.

## 3. Redis — trạng thái hiện tại

**Không dùng 10.000 key riêng lẻ** (`target:current:0001`, `target:current:0002`...) vì mỗi tick 300ms sẽ tạo ~33.000 lệnh SET/giây, tốn round-trip và connection overhead.

**Khuyến nghị**: dùng 1 Hash duy nhất, ghi bằng pipeline/batch:

```
HSET target:current:all 0001 '{"lat":...,"lon":...,"alt":...,"classification":"FRIEND","updatedAt":...}'
```

- Ghi: `HMSET`/pipeline toàn bộ batch trong 1 round-trip.
- Đọc toàn bộ (`GET /api/targets`): `HGETALL target:current:all`.
- Đọc 1 target (`GET /api/targets/{id}`): `HGET target:current:all {id}`.

Nếu cần TTL riêng từng target (target "biến mất" khỏi bản đồ khi ngừng cập nhật), cân nhắc thêm cơ chế dọn dẹp định kỳ (scheduled job so sánh `updatedAt`) thay vì TTL per-key, vì Hash field không hỗ trợ TTL riêng lẻ trong Redis bản chuẩn.

## 4. ClickHouse — lịch sử

```sql
CREATE TABLE target_position_history
(
    target_id String,
    timestamp DateTime64(3),
    latitude Float64,
    longitude Float64,
    altitude Float64,
    classification LowCardinality(String)
)
ENGINE = MergeTree
PARTITION BY toDate(timestamp)
ORDER BY (target_id, timestamp)
TTL timestamp + INTERVAL 7 DAY;
```

- **TTL 7 ngày** khớp đúng yêu cầu tối thiểu — nếu sau này cần giữ lâu hơn, chỉ cần đổi `INTERVAL 7 DAY`, không đổi schema.
- Ghi: bulk insert 1 lần/batch (~10.000 row/tick, ~33.000 row/giây ở tick 300ms). Không insert từng row.
- Đọc lịch sử dài: dùng tham số `sampleMs` để downsample, không trả hàng triệu điểm thô cho frontend.

## 5. PostgreSQL — metadata

```sql
CREATE TABLE target_metadata (
    target_id CHAR(4) PRIMARY KEY,
    classification VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

Chỉ dùng khi cần lưu thông tin tĩnh/ít đổi về target (không phải state realtime). Nếu metadata tối giản và không cần query quan hệ phức tạp, có thể bỏ Postgres và gộp vào Redis/ClickHouse — quyết định khi implement backend, không bắt buộc dùng Postgres ngay từ đầu.

## 6. WebSocket

- Topic broadcast: `/topic/targets/realtime`
- Payload: batch giống Kafka message (rút gọn field nếu cần).
- Bật `permessage-deflate` (nén) vì payload 10.000 object/300ms có thể vài trăm KB.
- Tối ưu về sau: chuyển sang binary frame (protobuf/flatbuffers) nếu JSON parse ở frontend trở thành bottleneck.

## 7. API

```
GET /api/targets
GET /api/targets/{targetId}
GET /api/targets/{targetId}/history?from=&to=&sampleMs=
```

Response mẫu `GET /api/targets`:

```json
[
  {
    "targetId": "0001",
    "latitude": 21.0285,
    "longitude": 105.8542,
    "altitude": 1200,
    "classification": "FRIEND",
    "updatedAt": 1783072800300
  }
]
```

## 8. Frontend rendering

- deck.gl `ScatterplotLayer`/`IconLayer` — không dùng HTML marker cho 10.000 điểm.
- Với > 5.000 điểm, ưu tiên truyền data dạng **typed array** (`Float32Array` cho lat/lon/alt) thay vì mảng object thường — giảm overhead GC và tăng tốc render đáng kể so với array-of-objects.
- Zustand store update theo batch (1 lần/tick), không setState từng target.

## 9. Điểm cần theo dõi khi tối ưu (phase cuối)

- Kafka consumer lag.
- Redis memory & pipeline latency.
- ClickHouse insert rate / merge performance.
- WebSocket payload size theo thời gian.
- Frontend frame rate khi hiển thị đủ 10.000 điểm + trail lịch sử.
