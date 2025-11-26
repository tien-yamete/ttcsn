# Identity Service

## 📖 Mô tả

Service xác thực và phân quyền: đăng ký, đăng nhập, quản lý JWT token, xác thực email, đặt lại mật khẩu.

## 🚀 Tính năng

- ✅ Đăng ký với xác thực email (OTP)
- ✅ Đăng nhập với JWT
- ✅ Refresh token
- ✅ Đặt lại mật khẩu (OTP)
- ✅ Quản lý user (CRUD)
- ✅ Role-based access control (RBAC)

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/auth/registration` | Đăng ký |
| POST | `/auth/verify-user` | Xác thực email |
| POST | `/auth/token` | Đăng nhập |
| POST | `/auth/refresh` | Refresh token |
| POST | `/auth/logout` | Đăng xuất |
| POST | `/auth/forgot-password` | Quên mật khẩu |
| POST | `/auth/reset-password` | Đặt lại mật khẩu |
| GET | `/users/myInfo` | Thông tin user hiện tại |

## 🔧 Cấu hình

- **Port**: 8081
- **Context Path**: `/identity`
- **Database**: MySQL
- **JWT**: Configurable secret key

## 🚀 Chạy

```bash
cd identity-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8081/identity/swagger-ui.html`
