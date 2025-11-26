# Notification Service

## 📖 Mô tả

Service thông báo: nhận events từ Kafka và gửi email qua SendGrid.

## 🚀 Tính năng

- ✅ Event-driven notifications (Kafka)
- ✅ Email notifications (SendGrid)
- ✅ HTML email support
- ✅ Error handling & retry

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/notifications/send` | Gửi thông báo thủ công |

**Lưu ý**: Hầu hết thông báo được gửi tự động qua Kafka events.

## 🔧 Cấu hình

- **Port**: 8083
- **Context Path**: `/notification`
- **Kafka Topic**: `notification-delivery`
- **SendGrid**: Cấu hình trong Config Server

## 🚀 Chạy

```bash
cd notification-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8083/notification/swagger-ui.html`
