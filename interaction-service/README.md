# Interaction Service 💬❤️

Service quản lý các tương tác của người dùng với posts và comments, bao gồm Comments và Likes/Reactions.

## 📋 Chức năng

### 1. Comments (Bình luận)
- ✅ Tạo comment cho post
- ✅ Tạo reply cho comment (nested comments)
- ✅ Lấy danh sách comments của post (có phân trang)
- ✅ Cập nhật comment
- ✅ Xóa comment (tự động xóa replies và likes)

### 2. Likes/Reactions (Thích)
- ✅ Like post
- ✅ Like comment
- ✅ Unlike post/comment
- ✅ Lấy danh sách likes của post (có phân trang)

## 🗄️ Database

### Tables
- **`comments`**: Lưu trữ comments và replies
  - `id` (UUID)
  - `post_id` (String)
  - `user_id` (String)
  - `content` (TEXT)
  - `parent_comment_id` (String, nullable - cho replies)
  - `created_at` (Instant)
  - `updated_at` (Instant)

- **`likes`**: Lưu trữ likes cho posts và comments
  - `id` (UUID)
  - `user_id` (String)
  - `post_id` (String, nullable)
  - `comment_id` (String, nullable)
  - `created_at` (Instant)
  - Unique constraint: `(user_id, post_id, comment_id)`

## 🔌 APIs

### Comments APIs

#### `POST /api/comments`
Tạo comment mới

**Request Body:**
```json
{
  "postId": "post-id",
  "content": "Nội dung comment",
  "parentCommentId": "parent-comment-id" // Optional: cho replies
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Tạo comment thành công",
  "result": {
    "id": "comment-id",
    "postId": "post-id",
    "userId": "user-id",
    "username": "username",
    "userAvatar": "avatar-url",
    "content": "Nội dung comment",
    "parentCommentId": null,
    "replies": [],
    "replyCount": 0,
    "likeCount": 0,
    "isLiked": false,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

#### `GET /api/comments/post/{postId}`
Lấy danh sách comments của post

**Query Parameters:**
- `page` (default: 1)
- `size` (default: 10)

**Response:**
```json
{
  "code": 200,
  "message": "Lấy danh sách comments thành công",
  "result": {
    "content": [...],
    "page": 1,
    "size": 10,
    "totalElements": 50,
    "totalPages": 5,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

#### `PUT /api/comments/{id}`
Cập nhật comment

**Request Body:**
```json
{
  "content": "Nội dung comment đã cập nhật"
}
```

#### `DELETE /api/comments/{id}`
Xóa comment (tự động xóa replies và likes)

### Likes APIs

#### `POST /api/likes`
Tạo like mới

**Request Body:**
```json
{
  "postId": "post-id" // Hoặc "commentId": "comment-id"
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Like thành công",
  "result": {
    "id": "like-id",
    "userId": "user-id",
    "username": "username",
    "userAvatar": "avatar-url",
    "postId": "post-id",
    "commentId": null,
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

#### `DELETE /api/likes/{id}`
Unlike bằng like ID

#### `DELETE /api/likes/post/{postId}`
Unlike post

#### `DELETE /api/likes/comment/{commentId}`
Unlike comment

#### `GET /api/likes/post/{postId}`
Lấy danh sách likes của post

**Query Parameters:**
- `page` (default: 1)
- `size` (default: 10)

## 📨 Events Published

### Comment Events
- **Topic**: `comment.events`
- **Events**:
  - `comment.created` - Khi tạo comment mới
  - `comment.updated` - Khi cập nhật comment
  - `comment.deleted` - Khi xóa comment

**Event Structure:**
```json
{
  "commentId": "comment-id",
  "postId": "post-id",
  "userId": "user-id",
  "eventType": "CREATED|UPDATED|DELETED",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Like Events
- **Topic**: `like.events`
- **Events**:
  - `like.created` - Khi tạo like mới
  - `like.deleted` - Khi unlike

**Event Structure:**
```json
{
  "likeId": "like-id",
  "userId": "user-id",
  "postId": "post-id",
  "commentId": "comment-id",
  "eventType": "CREATED|DELETED",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 📥 Events Consumed

### Post Events
- **Topic**: `post.events`
- **Events**:
  - `post.created` - Validate post exists
  - `post.deleted` - Xóa tất cả comments và likes của post

### User Events
- **Topic**: `user.events`
- **Events**:
  - `user.created` - Validate user exists

## 🔧 Configuration

### Port
- Default: `8088`
- Context path: `/interaction`

### Database
- MySQL
- Database name: `interaction_service`
- Auto-create: `true`

### Kafka
- Bootstrap servers: `localhost:9094`
- Consumer group: `interaction-service-group`

### External Services
- **Post Service**: `http://localhost:8084/post`
- **Profile Service**: `http://localhost:8082/profile`

## 🛡️ Security

- OAuth2 Resource Server với JWT
- Custom JWT Decoder
- Tất cả endpoints yêu cầu authentication (trừ `/internal/**`)

## 📦 Dependencies

- Spring Boot 3.5.5
- Spring Data JPA
- MySQL Connector
- Spring Cloud OpenFeign
- Spring Kafka
- Spring Security OAuth2 Resource Server
- Lombok
- MapStruct

## 🚀 Running

1. Đảm bảo MySQL và Kafka đang chạy
2. Đảm bảo Post Service và Profile Service đang chạy
3. Build và run:
```bash
cd interaction-service
mvn clean install
mvn spring-boot:run
```

## 📝 Notes

- Comments hỗ trợ nested replies (replies của replies)
- Khi xóa post, tất cả comments và likes liên quan sẽ tự động bị xóa
- Like có unique constraint để tránh duplicate likes
- Tất cả APIs trả về thông tin user (username, avatar) từ Profile Service

