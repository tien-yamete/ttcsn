# Friendify - Social Network (Microservice Architecture)

## 📖 Giới thiệu

**Friendify** là nền tảng mạng xã hội được xây dựng với kiến trúc **Microservice**. Dự án này là một phần của khóa học thực tập đại học, nhằm cung cấp kinh nghiệm thực tế trong việc phát triển hệ thống phân tán có khả năng mở rộng.

## 🏗️ Kiến trúc hệ thống

| Service | Port | Mô tả |
|---------|------|-------|
| **API Gateway** | 8080 | Điểm vào chính cho tất cả requests |
| **Config Server** | 8888 | Quản lý cấu hình tập trung |
| **Identity Service** | 8081 | Đăng ký, đăng nhập, xác thực JWT |
| **Profile Service** | 8082 | Quản lý profile người dùng |
| **Notification Service** | 8083 | Thông báo qua Kafka và SendGrid |
| **Post Service** | 8084 | Quản lý bài đăng, lưu, chia sẻ |
| **File Service** | 8085 | Upload file và media (Cloudinary) |
| **Chat Service** | 8086 | Chat real-time với WebSocket |
| **Social Service** | 8087 | Kết bạn, follow, block |
| **Interaction Service** | 8088 | Comment và like |
| **Group Service** | 8089 | Quản lý nhóm, thành viên, quyền |

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.5.5, Spring Cloud
- **Database**: MySQL, MongoDB
- **Message Queue**: Apache Kafka
- **Cache**: Redis
- **Authentication**: JWT, OAuth2
- **APIs**: Swagger (Springdoc OpenAPI)
- **Storage**: Cloudinary (media)
- **Email**: SendGrid

## 🚀 Cài đặt

### Yêu cầu
- Java 17+, Maven 3.6+
- MySQL 8.0+, MongoDB 6.0+
- Redis 6.0+, Apache Kafka 3.0+

### Chạy services

1. **Khởi động infrastructure**: MySQL, MongoDB, Redis, Kafka

2. **Khởi động services theo thứ tự**:
   ```bash
   # 1. Config Server (8888)
   # 2. API Gateway (8080)
   # 3. Identity Service (8081)
   # 4. Các service còn lại
   ```

3. **Build và chạy**:
   ```bash
   mvn clean install
   cd <service-name>
   mvn spring-boot:run
   ```

4. **Truy cập**:
   - API Gateway: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

## 📌 Tính năng chính

- ✅ Đăng ký/đăng nhập với JWT, xác thực email OTP
- ✅ Quản lý profile, avatar, background
- ✅ Đăng bài với hình ảnh, privacy settings
- ✅ Comment, like, share bài đăng
- ✅ Kết bạn, follow, block
- ✅ Chat real-time (1-1 và group)
- ✅ Quản lý nhóm với quyền hạn
- ✅ Thông báo qua email và Kafka

## 📂 Cấu trúc dự án

```
microservice-social-network/
├── api-gateway/          # API Gateway
├── config-server/        # Config Server
├── identity-service/     # Authentication
├── profile-service/      # User profiles
├── notification-service/ # Notifications
├── post-service/         # Posts
├── file-service/         # File uploads
├── chat-service/         # Real-time chat
├── social-service/       # Friendships
├── interaction-service/ # Comments & likes
├── group-service/        # Groups
├── shared-common/       # Shared utilities
└── shared-contacts/     # Shared contacts
```

## 👨‍💻 Tác giả

**Tạ Văn Tiến** - Dự án khóa học thực tập

## 📜 License

Dự án này được tạo cho mục đích **giáo dục** và không dùng cho mục đích thương mại.
