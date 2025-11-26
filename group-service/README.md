# Group Service

## 📖 Mô tả

Service quản lý nhóm: tạo nhóm, quản lý thành viên, phân quyền, join requests.

## 🚀 Tính năng

- ✅ CRUD nhóm
- ✅ Quản lý thành viên (add/remove)
- ✅ Phân quyền: ADMIN, MODERATOR, MEMBER
- ✅ Join requests và approval
- ✅ Privacy settings: PUBLIC, PRIVATE, CLOSED
- ✅ Posting permissions

## 🔌 API chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/groups` | Tạo nhóm |
| GET | `/groups/{groupId}` | Lấy chi tiết |
| PUT | `/groups/{groupId}` | Cập nhật (owner only) |
| DELETE | `/groups/{groupId}` | Xóa (owner only) |
| POST | `/groups/{groupId}/members/{userId}` | Thêm thành viên |
| DELETE | `/groups/{groupId}/members/{userId}` | Xóa thành viên |
| PUT | `/groups/{groupId}/members/{userId}/role` | Cập nhật role |
| POST | `/groups/{groupId}/join-requests` | Gửi join request |
| POST | `/groups/{groupId}/join-requests/{requestId}/process` | Xử lý request |

## 🔧 Cấu hình

- **Port**: 8089
- **Context Path**: `/group`
- **Database**: MongoDB

## 🚀 Chạy

```bash
cd group-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8089/group/swagger-ui.html`

