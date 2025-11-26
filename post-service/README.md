# Post Service

## 📖 Mô tả

Service quản lý bài đăng: tạo, sửa, xóa, lưu, chia sẻ bài đăng với hỗ trợ hình ảnh và privacy settings.

## 🚀 Tính năng

- ✅ CRUD bài đăng (text + images)
- ✅ Privacy: PUBLIC, FRIENDS, PRIVATE
- ✅ Lưu bài đăng (bookmark)
- ✅ Chia sẻ bài đăng
- ✅ Tìm kiếm bài đăng
- ✅ Phân trang

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/create` | Tạo bài đăng |
| GET | `/posts/{postId}` | Lấy chi tiết |
| PUT | `/posts/{postId}` | Cập nhật |
| DELETE | `/posts/{postId}` | Xóa |
| GET | `/posts/my-posts` | Bài đăng của mình |
| POST | `/posts/save/{postId}` | Lưu bài đăng |
| POST | `/posts/share/{postId}` | Chia sẻ bài đăng |
| GET | `/posts/search?keyword=...` | Tìm kiếm |

## 🔧 Cấu hình

- **Port**: 8084
- **Context Path**: `/post`
- **Database**: MongoDB
- **Kafka**: `post.events`

## 🚀 Chạy

```bash
cd post-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8084/post/swagger-ui.html`
