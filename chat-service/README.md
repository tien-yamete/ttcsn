# Chat Service

## 📖 Mô tả

Service chat real-time: tin nhắn 1-1 và group chat với WebSocket, read receipts, typing indicators.

## 🚀 Tính năng

- ✅ Chat 1-1 (DIRECT) - tự động tạo
- ✅ Group chat (GROUP) - tự động tạo
- ✅ WebSocket real-time
- ✅ Read receipts
- ✅ Typing indicators
- ✅ Sửa/xóa tin nhắn
- ✅ Quản lý thành viên (GROUP)
- ✅ Role-based permissions (ADMIN/MEMBER)

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/conversations/create` | Tạo conversation |
| GET | `/conversations/my-conversations` | Danh sách conversation |
| POST | `/messages/create` | Gửi tin nhắn |
| GET | `/messages/paginated?conversationId=...` | Lấy tin nhắn (phân trang) |
| POST | `/messages/{id}/read` | Đánh dấu đã đọc |

## 🔌 WebSocket

- **Endpoint**: `ws://localhost:8086/chat/ws`
- **Subscribe**: `/topic/conversation/{conversationId}`
- **Send**: `/app/chat.sendMessage`

## 🔧 Cấu hình

- **Port**: 8086
- **Context Path**: `/chat`
- **Database**: MongoDB

## 🚀 Chạy

```bash
cd chat-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8086/chat/swagger-ui.html`
