# Interaction Service

## 📖 Mô tả

Service quản lý tương tác: comment (có nested replies) và like/reaction cho posts và comments.

## 🚀 Tính năng

- ✅ Comment trên post
- ✅ Reply comment (nested replies)
- ✅ Like post/comment
- ✅ Phân trang comments/likes
- ✅ Auto-cleanup khi xóa post

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/comments` | Tạo comment |
| GET | `/api/comments/post/{postId}` | Lấy comments (phân trang) |
| PUT | `/api/comments/{id}` | Sửa comment |
| DELETE | `/api/comments/{id}` | Xóa comment |
| POST | `/api/likes` | Like post/comment |
| DELETE | `/api/likes/post/{postId}` | Unlike post |
| GET | `/api/likes/post/{postId}` | Lấy danh sách likes |

## 🔧 Cấu hình

- **Port**: 8088
- **Context Path**: `/interaction`
- **Database**: MySQL
- **Kafka**: `comment.events`, `like.events`

## 🚀 Chạy

```bash
cd interaction-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8088/interaction/swagger-ui.html`
