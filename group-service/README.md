# Group Service

## 📖 Mô tả

Service quản lý nhóm: tạo nhóm, quản lý thành viên, phân quyền, join requests, và các cài đặt đăng bài.

## 🚀 Tính năng

- ✅ CRUD nhóm (avatar, cover image)
- ✅ Quản lý thành viên (add/remove, phân trang)
- ✅ Phân quyền: ADMIN, MODERATOR, MEMBER
- ✅ Join/leave nhóm
- ✅ Join requests và approval workflow
- ✅ Privacy settings: PUBLIC, PRIVATE, CLOSED
- ✅ Posting permissions:
  - `allowPosting` - Cho phép đăng bài
  - `onlyAdminCanPost` - Chỉ admin/moderator được đăng
  - `moderationRequired` - Cần kiểm duyệt bài đăng
  - `requiresApproval` - Cần phê duyệt khi tham gia
- ✅ Tìm kiếm nhóm
- ✅ Lấy nhóm của mình / nhóm đã tham gia
- ✅ Lấy tất cả nhóm (PUBLIC/CLOSED) với phân trang
- ✅ Kiểm tra quyền xem post dựa trên privacy:
  - **PUBLIC**: Ai cũng xem được post
  - **PRIVATE**: Chỉ thành viên mới xem được post
  - **CLOSED**: Ai cũng xem được post (nhưng cần join mới tham gia)

## 🔌 API chính

### Group CRUD
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/groups` | Tạo nhóm |
| GET | `/groups` | Lấy tất cả nhóm (PUBLIC/CLOSED, phân trang, có thể filter theo privacy) |
| GET | `/groups/{groupId}` | Lấy chi tiết |
| PUT | `/groups/{groupId}` | Cập nhật (owner only) |
| DELETE | `/groups/{groupId}` | Xóa (owner only) |

### Member Management
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/groups/{groupId}/members` | Lấy danh sách thành viên (phân trang, có thể filter theo role: ADMIN/MODERATOR/MEMBER) |
| POST | `/groups/{groupId}/members/{userId}` | Thêm thành viên (admin only) |
| DELETE | `/groups/{groupId}/members/{userId}` | Xóa thành viên (admin only) |
| PUT | `/groups/{groupId}/members/{userId}/role` | Cập nhật role (admin only) |

### Join/Leave
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/groups/{groupId}/join` | Join nhóm |
| POST | `/groups/{groupId}/leave` | Rời nhóm |

### Join Requests
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/groups/{groupId}/join-requests` | Lấy join requests (admin/moderator only, phân trang) |
| POST | `/groups/{groupId}/join-requests/{requestId}/process` | Xử lý request (approve/reject) |
| DELETE | `/groups/{groupId}/join-requests/{requestId}` | Hủy join request của mình |
| GET | `/my-join-requests` | Lấy tất cả join requests mình đã gửi (phân trang) |

### Query Operations
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/my-groups` | Lấy nhóm của mình (owner, phân trang) |
| GET | `/joined-groups` | Lấy nhóm đã tham gia (phân trang) |
| GET | `/search?keyword=...` | Tìm kiếm nhóm (phân trang) |

### Internal APIs
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/internal/groups/{groupId}/exists` | Kiểm tra nhóm tồn tại |
| GET | `/internal/groups/{groupId}` | Lấy nhóm (internal) |
| GET | `/internal/groups/{groupId}/can-post` | Kiểm tra quyền đăng bài |
| GET | `/internal/groups/{groupId}/can-view` | Kiểm tra quyền xem post trong nhóm |
| GET | `/internal/groups/{groupId}/can-view/{userId}` | Kiểm tra quyền xem post (với userId cụ thể) |

## 🔧 Cấu hình

- **Port**: 8089
- **Context Path**: `/group`
- **Database**: MongoDB
- **External Services**: Profile Service (lấy thông tin user)

## 🚀 Chạy

```bash
cd group-service
mvn spring-boot:run
```

**Truy cập**: `http://localhost:8089/group/swagger-ui.html`
