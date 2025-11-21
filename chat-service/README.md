# Chat Service - Feature Checklist

## ✅ Đã có đầy đủ chức năng cho Chat 2 người và Group Chat

### 📋 1. Conversation Management

#### ✅ Chat 2 người (DIRECT)
- ✅ Tạo conversation DIRECT với 1 participant khác
- ✅ Tự động tìm conversation đã tồn tại (dựa trên participantsHash)
- ✅ Hiển thị tên/avatar của người kia
- ✅ Xóa conversation khi user leave

#### ✅ Group Chat (GROUP)
- ✅ Tạo conversation GROUP với nhiều participants
- ✅ Thêm participants vào group
- ✅ Xóa participants khỏi group
- ✅ Update tên/avatar của group
- ✅ User có thể leave group
- ✅ Tự động xóa group nếu chỉ còn 1 người

### 📋 2. Conversation APIs

| API | Method | Endpoint | Mô tả |
|-----|--------|----------|-------|
| Tạo conversation | POST | `/conversations/create` | Tạo DIRECT hoặc GROUP |
| Lấy danh sách | GET | `/conversations/my-conversations` | Lấy tất cả conversations của user |
| Lấy chi tiết | GET | `/conversations/{id}` | Lấy thông tin conversation |
| Cập nhật | PUT | `/conversations/{id}` | Chỉ GROUP: update tên/avatar |
| Xóa | DELETE | `/conversations/{id}` | Xóa conversation |
| Thêm thành viên | POST | `/conversations/{id}/participants` | Chỉ GROUP: thêm participants |
| Xóa thành viên | DELETE | `/conversations/{id}/participants/{participantId}` | Chỉ GROUP: xóa participant |
| Rời khỏi | POST | `/conversations/{id}/leave` | DIRECT: xóa, GROUP: remove user |

### 📋 3. Chat Message APIs

| API | Method | Endpoint | Mô tả |
|-----|--------|----------|-------|
| Gửi tin nhắn | POST | `/messages/create` | Gửi message vào conversation |
| Lấy messages | GET | `/messages?conversationId={id}` | Lấy tất cả messages |
| Lấy messages (pagination) | GET | `/messages/paginated?conversationId={id}&page={page}&size={size}` | Lấy messages có phân trang |
| Lấy message | GET | `/messages/{id}` | Lấy chi tiết 1 message |
| Sửa message | PUT | `/messages/{id}` | Chỉ sender mới sửa được |
| Xóa message | DELETE | `/messages/{id}` | Chỉ sender mới xóa được |
| Đánh dấu đã đọc | POST | `/messages/{id}/read` | Mark message as read |
| Lấy read receipts | GET | `/messages/{id}/read-receipts` | Xem ai đã đọc message |
| Đếm unread | GET | `/messages/unread-count?conversationId={id}` | Đếm số tin nhắn chưa đọc |

### 📋 4. WebSocket Real-time Features

| Feature | Destination | Mô tả |
|---------|-------------|-------|
| Gửi tin nhắn | `/app/chat.sendMessage` | Gửi message real-time |
| Typing indicator | `/app/chat.typing` | Hiển thị "đang gõ..." |
| User join | `/app/chat.addUser` | Thông báo user tham gia |
| User leave | `/app/chat.removeUser` | Thông báo user rời khỏi |

#### Subscribe Topics:
- `/topic/conversation/{conversationId}` - Nhận messages
- `/topic/conversation/{conversationId}/typing` - Nhận typing indicators
- `/user/queue/errors` - Nhận error messages

### 📋 5. Security & Validation

- ✅ JWT Authentication cho tất cả APIs
- ✅ WebSocket Authentication với JWT token
- ✅ Validate user là participant trước khi truy cập
- ✅ Validate chỉ sender mới sửa/xóa được message
- ✅ Validate chỉ GROUP mới có thể update/add/remove participants
- ✅ Validate DIRECT chỉ có 2 người

### 📋 6. Data Models

#### Conversation Entity
- ✅ `id` - Unique identifier
- ✅ `typeConversation` - DIRECT hoặc GROUP
- ✅ `participantsHash` - Hash để tìm conversation đã tồn tại
- ✅ `participants` - Danh sách participants với đầy đủ thông tin
- ✅ `conversationName` - Tên conversation (GROUP) hoặc tên người kia (DIRECT)
- ✅ `conversationAvatar` - Avatar conversation (GROUP) hoặc avatar người kia (DIRECT)
- ✅ `createdDate` - Ngày tạo
- ✅ `modifiedDate` - Ngày sửa đổi

#### ChatMessage Entity
- ✅ `id` - Unique identifier
- ✅ `conversationId` - ID của conversation
- ✅ `message` - Nội dung tin nhắn
- ✅ `sender` - Thông tin người gửi (ParticipantInfo)
- ✅ `createdDate` - Thời gian gửi

#### ReadReceipt Entity
- ✅ `id` - Unique identifier
- ✅ `messageId` - ID của message
- ✅ `conversationId` - ID của conversation
- ✅ `userId` - ID của user đã đọc
- ✅ `readAt` - Thời gian đọc

### 📋 7. Tính năng bổ sung

- ✅ Read receipts - Xem ai đã đọc message
- ✅ Unread count - Đếm số tin nhắn chưa đọc
- ✅ Pagination - Phân trang messages
- ✅ Message edit/delete - Sửa/xóa message
- ✅ Typing indicator - Hiển thị đang gõ
- ✅ User join/leave notifications - Thông báo tham gia/rời khỏi

### ⚠️ Lưu ý

1. **Tạo DIRECT conversation**: 
   - Chỉ cần 1 participant trong `participantIds`
   - System tự động thêm current user
   - Tự động tìm conversation đã tồn tại

2. **Tạo GROUP conversation**:
   - Có thể có nhiều participants trong `participantIds`
   - System tự động thêm current user
   - Mỗi group là unique (dựa trên participantsHash)

3. **ParticipantsHash**:
   - DIRECT: Hash từ 2 user IDs (sorted)
   - GROUP: Hash từ tất cả user IDs (sorted)
   - Dùng để tìm conversation đã tồn tại

4. **Validation**:
   - DIRECT: Chỉ có thể có 2 người
   - GROUP: Có thể có nhiều người
   - Chỉ GROUP mới có thể update name/avatar, add/remove participants

## ✅ Kết luận

**Chat Service đã đầy đủ chức năng cho:**
- ✅ Chat 2 người (DIRECT)
- ✅ Group chat (GROUP)
- ✅ Tất cả các API cần thiết
- ✅ WebSocket real-time
- ✅ Read receipts & unread count
- ✅ Security & validation

**Code đã được sửa để hỗ trợ đầy đủ GROUP chat với nhiều participants!**

