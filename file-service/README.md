# File Service

## 📖 Mô tả

Service upload file và media, tích hợp với Cloudinary để lưu trữ và quản lý hình ảnh.

## 🚀 Tính năng

- ✅ Upload 1 hoặc nhiều hình ảnh
- ✅ Phân loại: AVATAR, POST, BACKGROUND
- ✅ Tích hợp Cloudinary
- ✅ Tối ưu hình ảnh tự động

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/images/upload-form-data` | Upload 1 ảnh (form) |
| POST | `/images/upload-multiple-form-data` | Upload nhiều ảnh |
| POST | `/images/upload` | Upload ảnh (JSON) |

## 🔧 Cấu hình

- **Port**: 8085
- **Context Path**: `/file`
- **Cloudinary**: Cấu hình trong Config Server
- **Max file size**: 10MB

## 🚀 Chạy

```bash
cd file-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8085/file/swagger-ui.html`
