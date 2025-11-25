# 🚀 Hướng dẫn chạy Swagger UI

## Bước 1: Build các service

### Build shared modules trước:
```bash
cd shared-common
mvn clean install -DskipTests

cd ../shared-contacts
mvn clean install -DskipTests
```

### Build từng service (hoặc build tất cả):
```bash
# Từ thư mục gốc
mvn clean install -DskipTests
```

## Bước 2: Chạy các service

**Quan trọng:** Chạy theo thứ tự sau:

### 1. Config Server (Port 8888) - BẮT BUỘC chạy đầu tiên
```bash
cd config-server
mvn spring-boot:run
```

### 2. API Gateway (Port 8080)
```bash
cd api-gateway
mvn spring-boot:run
```

### 3. Identity Service (Port 8081)
```bash
cd identity-service
mvn spring-boot:run
```

### 4. Các service khác (có thể chạy song song):
```bash
# Profile Service (Port 8082)
cd profile-service
mvn spring-boot:run

# Notification Service (Port 8083)
cd notification-service
mvn spring-boot:run

# Post Service (Port 8084)
cd post-service
mvn spring-boot:run

# File Service (Port 8085)
cd file-service
mvn spring-boot:run

# Chat Service (Port 8086)
cd chat-service
mvn spring-boot:run

# Social Service (Port 8087)
cd social-service
mvn spring-boot:run

# Interaction Service (Port 8088)
cd interaction-service
mvn spring-boot:run

# Group Service (Port 8089)
cd group-service
mvn spring-boot:run
```

## Bước 3: Truy cập Swagger UI

Sau khi service đã chạy, mở trình duyệt và truy cập:

### Truy cập trực tiếp từng service (VỚI CONTEXT-PATH):

| Service | URL Swagger UI |
|---------|----------------|
| **Identity Service** | http://localhost:8081/identity/swagger-ui.html |
| **Profile Service** | http://localhost:8082/profile/swagger-ui.html |
| **Notification Service** | http://localhost:8083/notification/swagger-ui.html |
| **Post Service** | http://localhost:8084/post/swagger-ui.html |
| **File Service** | http://localhost:8085/file/swagger-ui.html |
| **Chat Service** | http://localhost:8086/chat/swagger-ui.html |
| **Social Service** | http://localhost:8087/social/swagger-ui.html |
| **Interaction Service** | http://localhost:8088/interaction/swagger-ui.html |
| **Group Service** | http://localhost:8089/group/swagger-ui.html |
| **API Gateway** | http://localhost:8080/swagger-ui.html |

### Hoặc truy cập qua API Gateway:

- Identity: http://localhost:8080/identity/swagger-ui.html
- Profile: http://localhost:8080/profile/swagger-ui.html
- Notification: http://localhost:8080/notification/swagger-ui.html
- Post: http://localhost:8080/post/swagger-ui.html
- File: http://localhost:8080/file/swagger-ui.html
- Chat: http://localhost:8080/chat/swagger-ui.html
- Social: http://localhost:8080/social/swagger-ui.html
- Interaction: http://localhost:8080/interaction/swagger-ui.html
- Group: http://localhost:8080/group/swagger-ui.html

## Bước 4: Sử dụng Swagger UI

1. **Xem API Documentation**: Swagger UI sẽ hiển thị tất cả các endpoints của service
2. **Test API**: 
   - Click vào endpoint muốn test
   - Click "Try it out"
   - Điền thông tin vào các parameters
   - Click "Execute"
3. **Authentication**: 
   - Click nút "Authorize" ở đầu trang
   - Nhập JWT token: `Bearer <your-jwt-token>`
   - Click "Authorize"
   - Bây giờ bạn có thể test các API cần authentication

## Lưu ý:

- Đảm bảo Config Server chạy trước tất cả các service khác
- Đảm bảo các database (MySQL, MongoDB) đã được cấu hình và chạy
- Đảm bảo Kafka đã chạy (nếu service sử dụng Kafka)
- Swagger UI endpoints đã được cấu hình public, không cần authentication để truy cập

## Troubleshooting:

### Nếu Swagger UI không hiển thị:
1. Kiểm tra service đã chạy chưa: `curl http://localhost:8081/actuator/health`
2. Kiểm tra log của service để xem có lỗi gì không
3. Kiểm tra port có bị conflict không
4. Thử truy cập API docs trực tiếp: `http://localhost:8081/v3/api-docs`

### Nếu gặp lỗi 404:
- Kiểm tra context-path trong config
- Kiểm tra SecurityConfig đã cho phép Swagger endpoints chưa

