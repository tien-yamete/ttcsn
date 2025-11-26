# Social Service

## 📖 Mô tả

Service quản lý mối quan hệ xã hội: kết bạn, follow/unfollow, block user.

## 🚀 Tính năng

- ✅ Gửi/chấp nhận/từ chối lời mời kết bạn
- ✅ Follow/unfollow user
- ✅ Block/unblock user
- ✅ Tìm kiếm bạn bè
- ✅ Phân trang

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/friendships/{friendId}` | Gửi lời mời kết bạn |
| POST | `/friendships/{friendId}/accept` | Chấp nhận |
| POST | `/friendships/{friendId}/reject` | Từ chối |
| DELETE | `/friendships/{friendId}` | Xóa bạn |
| GET | `/friendships/friends` | Danh sách bạn |
| POST | `/follows/{userId}` | Follow user |
| DELETE | `/follows/{userId}` | Unfollow |
| POST | `/blocks/{userId}` | Block user |

## 🔧 Cấu hình

- **Port**: 8087
- **Context Path**: `/social`
- **Database**: MySQL

## 🚀 Chạy

```bash
cd social-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8087/social/swagger-ui.html`
