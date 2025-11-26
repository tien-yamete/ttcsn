# Profile Service

## 📖 Mô tả

Service quản lý profile người dùng: thông tin cá nhân, avatar, background, tìm kiếm user.

## 🚀 Tính năng

- ✅ CRUD profile
- ✅ Upload avatar
- ✅ Upload background
- ✅ Tìm kiếm user
- ✅ Batch profile retrieval (internal API)

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/users/{profileId}` | Lấy profile |
| GET | `/users/my-profile` | Profile của mình |
| PUT | `/users/my-profile` | Cập nhật profile |
| PUT | `/users/avatar` | Upload avatar |
| PUT | `/users/background` | Upload background |
| POST | `/users/search` | Tìm kiếm user |
| GET | `/internal/users/{userId}` | Internal API |

## 🔧 Cấu hình

- **Port**: 8082
- **Context Path**: `/profile`
- **Database**: MySQL

## 🚀 Chạy

```bash
cd profile-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8082/profile/swagger-ui.html`
