# Flight Tracking Realtime System

Hệ thống mô phỏng, xử lý và hiển thị thời gian thực vị trí mục tiêu bay trên bản đồ số.

Luồng chính:

```text
Simulator -> Kafka -> Backend -> Redis / ClickHouse / WebSocket -> Frontend
```

Mặc định hệ thống chạy với:

| Thành phần | Giá trị |
|---|---|
| Số mục tiêu mô phỏng | 10.000 target |
| Chu kỳ cập nhật | 300ms |
| Backend API | `http://localhost:8080` |
| WebSocket | `ws://localhost:8080/ws` |
| Frontend | `http://localhost:5173` |
| Kafka | `localhost:9092` |
| Redis | `localhost:6379` |
| ClickHouse | `localhost:8123` |
| PostgreSQL | `localhost:5433` |

## Yêu cầu môi trường

| Công cụ | Phiên bản khuyến nghị | Ghi chú |
|---|---|---|
| Java JDK | 21 | Backend và simulator dùng Java 21 |
| Maven | 3.9+ | Project hiện chưa có Maven wrapper, cần cài `mvn` |
| Node.js | 20+ | Frontend Vite/React |
| npm | 10+ | Cài dependency frontend |
| Docker Desktop | Bản mới | Chạy Kafka, Redis, ClickHouse, PostgreSQL |

Kiểm tra nhanh:

```powershell
java -version
mvn -version
node -v
npm -v
docker --version
docker compose version
```

## Cấu trúc project

```text
flight-tracking-system/
├── common/flight-common       # DTO, enum, event dùng chung
├── backend/flight-backend     # Spring Boot backend
├── simulator/flight-simulator # Spring Boot simulator
├── frontend/flight-web        # React + Vite frontend
├── docker-compose.yml         # Kafka, Redis, ClickHouse, PostgreSQL
└── pom.xml                    # Maven parent project
```

## Cài đặt và chạy

### 1. Clone project

```powershell
git clone <repository-url>
cd flight-tracking-system
```

### 2. Chạy hạ tầng bằng Docker

```powershell
docker compose up -d
```

Kiểm tra container:

```powershell
docker compose ps
```

Cần thấy các service sau đang chạy:

| Service | Container |
|---|---|
| Kafka | `flight-kafka` |
| Redis | `flight-redis` |
| ClickHouse | `flight-clickhouse` |
| PostgreSQL | `flight-postgres` |

### 3. Build backend, simulator và common

Chạy ở thư mục root:

```powershell
mvn clean install
```

Nếu chỉ muốn build nhanh không chạy test:

```powershell
mvn clean install -DskipTests
```

### 4. Chạy backend

Mở terminal mới tại thư mục root:

```powershell
cd backend/flight-backend
mvn spring-boot:run
```

Backend chạy tại:

```text
http://localhost:8080
```

Kiểm tra backend:

```powershell
curl http://localhost:8080/api/health
```

Kết quả mong đợi:

```json
{"status":"UP"}
```

### 5. Chạy simulator

Mở terminal mới tại thư mục root:

```powershell
cd simulator/flight-simulator
mvn spring-boot:run
```

Simulator sẽ sinh dữ liệu mặc định:

| Cấu hình | Giá trị |
|---|---|
| Số target | `10000` |
| Chu kỳ | `300ms` |
| Kafka topic | `flight-target-updates` |

Sau khi simulator chạy, backend sẽ bắt đầu nhận batch và đẩy realtime cho frontend.

### 6. Chạy frontend

Mở terminal mới tại thư mục root:

```powershell
cd frontend/flight-web
npm install
npm run dev
```

Frontend chạy tại:

```text
http://localhost:5173
```

Mở trình duyệt vào URL trên để xem bản đồ realtime.

## Cấu hình frontend tùy chọn

Frontend mặc định gọi backend tại `http://localhost:8080` và WebSocket tại `ws://localhost:8080/ws`.

Nếu cần đổi URL, tạo file:

```text
frontend/flight-web/.env.local
```

Nội dung ví dụ:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_WEBSOCKET_URL=ws://localhost:8080/ws
```

Sau khi đổi `.env.local`, khởi động lại frontend.

## Kiểm tra hệ thống sau khi chạy

### Backend health

```powershell
curl http://localhost:8080/api/health
```

### Runtime monitoring

```powershell
curl http://localhost:8080/api/monitoring/runtime
```

Endpoint này cho biết batch gần nhất, thời gian xử lý, Kafka lag, Redis memory và trạng thái có đạt mục tiêu 300ms hay không.

### Lấy danh sách target hiện tại

```powershell
curl http://localhost:8080/api/targets
```

### Lấy chi tiết một target

```powershell
curl http://localhost:8080/api/targets/0001
```

### Kiểm tra Redis

```powershell
docker exec -it flight-redis redis-cli
```

Trong Redis CLI:

```redis
HLEN target:current:all
HGET target:current:all 0001
```

### Kiểm tra ClickHouse

```powershell
docker exec -it flight-clickhouse clickhouse-client -u flight --password flight --database flight_tracking
```

Trong ClickHouse client:

```sql
SELECT count()
FROM target_position_history;
```

```sql
SELECT *
FROM target_position_history
WHERE target_id = '0001'
ORDER BY timestamp DESC
LIMIT 10;
```

## Chạy test

Chạy toàn bộ test Java:

```powershell
mvn test
```

Chạy test theo module:

```powershell
mvn -pl common/flight-common test
mvn -pl backend/flight-backend test
mvn -pl simulator/flight-simulator test
```

Build frontend:

```powershell
cd frontend/flight-web
npm run build
```

Lint frontend:

```powershell
cd frontend/flight-web
npm run lint
```

## Giảm tải khi máy yếu

Mặc định simulator chạy 10.000 target. Nếu máy yếu, có thể giảm số lượng target để kiểm tra chức năng trước.

Cách 1: sửa file:

```text
simulator/flight-simulator/src/main/resources/application.yml
```

Đổi:

```yml
simulator:
  target-count: 1000
```

Cách 2: truyền tham số khi chạy simulator:

```powershell
cd simulator/flight-simulator
mvn spring-boot:run -Dspring-boot.run.arguments="--simulator.target-count=1000"
```

Lưu ý: nghiệm thu cuối vẫn cần kiểm thử ở mức 10.000 target.

## Dừng hệ thống

Dừng backend, simulator và frontend bằng `Ctrl + C` ở từng terminal.

Dừng hạ tầng Docker:

```powershell
docker compose down
```

Nếu muốn xóa cả dữ liệu volume Docker:

```powershell
docker compose down -v
```

## Lỗi thường gặp

| Lỗi | Nguyên nhân thường gặp | Cách xử lý |
|---|---|---|
| Backend không kết nối Kafka | Kafka chưa chạy hoặc chưa sẵn sàng | Chạy `docker compose ps`, đợi Kafka start xong rồi chạy lại backend |
| Backend không kết nối Redis | Redis container chưa chạy | Kiểm tra container `flight-redis` |
| Backend không kết nối ClickHouse | ClickHouse chưa sẵn sàng | Đợi thêm vài giây hoặc restart backend |
| Frontend trắng dữ liệu | Backend hoặc simulator chưa chạy | Chạy backend trước, sau đó chạy simulator |
| Không có target trong Redis | Backend chưa consume được Kafka message | Kiểm tra simulator log, backend log và `/api/monitoring/runtime` |
| Port bị chiếm | Máy đang có service khác dùng port | Dừng service đang chiếm port hoặc đổi port trong config |
| Maven không tìm thấy `flight-common` | Chưa build parent project | Chạy `mvn clean install` ở thư mục root |

## Tài liệu liên quan

| File | Nội dung |
|---|---|
| `ARCHITECTURE.md` | Kiến trúc kỹ thuật, luồng dữ liệu, schema và API |
| `ROADMAP.md` | Lộ trình triển khai theo phase |
| `TAI_LIEU_BA_DU_AN.md` | Tài liệu yêu cầu và phân tích thiết kế theo góc nhìn BA |
| `BAO_CAO_DU_AN.md` | Báo cáo dự án |
| `KIENTRUC_HE_THONG.excalidraw` | Sơ đồ kiến trúc hệ thống dạng Excalidraw |
