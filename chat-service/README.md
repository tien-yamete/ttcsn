# Chat Service - Feature Checklist

## ✅ Đã có đầy đủ chức năng cho Chat 2 người và Group Chat

### 📋 1. Conversation Management

#### ✅ Chat 2 người (DIRECT)
- ✅ Tự động tạo conversation DIRECT khi có 1 participant khác (tổng 2 người)
- ✅ Tự động tìm conversation đã tồn tại (dựa trên participantsHash)
- ✅ Hiển thị tên/avatar của người kia
- ✅ **Tất cả participants đều là ADMIN** (cả hai có quyền như nhau)
- ✅ Xóa conversation khi user leave
- ✅ Cả hai đều có quyền update và delete conversation

#### ✅ Group Chat (GROUP)
- ✅ Tự động tạo conversation GROUP khi có 2+ participants khác (tổng 3+ người)
- ✅ **Chỉ creator là ADMIN**, các thành viên khác là MEMBER
- ✅ Thêm participants vào group (chỉ ADMIN)
- ✅ Xóa participants khỏi group (chỉ ADMIN)
- ✅ Update tên/avatar của group (chỉ ADMIN)
- ✅ Promote/demote admin (chỉ ADMIN, chỉ áp dụng với GROUP)
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
| Rời khỏi | POST | `/conversations/{id}/leave` | DIRECT: xóa conversation, GROUP: remove user |
| Promote admin | POST | `/conversations/{id}/admins` | Chỉ GROUP: thăng cấp thành viên lên admin |
| Demote admin | DELETE | `/conversations/{id}/admins/{participantId}` | Chỉ GROUP: hạ cấp admin xuống member |

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
- ✅ Validate chỉ GROUP mới có thể promote/demote admin
- ✅ **Tự động xác định loại conversation**: 1 người = DIRECT, 2+ người = GROUP
- ✅ **Role-based permissions**:
  - DIRECT: Tất cả participants đều là ADMIN (có quyền như nhau)
  - GROUP: Chỉ ADMIN mới có quyền quản lý (update, add/remove participants, promote/demote)

### 📋 6. Data Models

#### Conversation Entity
- ✅ `id` - Unique identifier
- ✅ `typeConversation` - DIRECT hoặc GROUP (tự động xác định)
- ✅ `participantsHash` - Hash để tìm conversation đã tồn tại
- ✅ `participants` - Danh sách participants với đầy đủ thông tin và role (ADMIN/MEMBER)
- ✅ `conversationName` - Tên conversation (GROUP) hoặc tên người kia (DIRECT)
- ✅ `conversationAvatar` - Avatar conversation (GROUP) hoặc avatar người kia (DIRECT)
- ✅ `createdDate` - Ngày tạo
- ✅ `modifiedDate` - Ngày sửa đổi

#### ParticipantInfo Entity
- ✅ `userId` - ID của user
- ✅ `username` - Username
- ✅ `firstName` - Tên
- ✅ `lastName` - Họ
- ✅ `avatar` - Avatar URL
- ✅ `role` - ADMIN hoặc MEMBER
  - **DIRECT**: Tất cả đều là ADMIN
  - **GROUP**: Creator là ADMIN, các thành viên khác là MEMBER

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

1. **Tạo Conversation (Tự động xác định loại)**:
   - Chỉ cần truyền `participantIds` (danh sách người tham gia, không bao gồm người tạo)
   - System tự động xác định loại:
     - **1 người** → DIRECT conversation (tổng 2 người)
     - **2+ người** → GROUP conversation (tổng 3+ người)
   - System tự động thêm current user vào participants
   - Tự động tìm conversation đã tồn tại (dựa trên participantsHash)

2. **Role Management**:
   - **DIRECT conversation**:
     - Tất cả participants đều có role **ADMIN**
     - Cả hai đều có quyền như nhau (update, delete)
     - Không có promote/demote admin
   - **GROUP conversation**:
     - Creator (người tạo) có role **ADMIN**
     - Các thành viên khác có role **MEMBER**
     - Chỉ ADMIN mới có quyền:
       - Update tên/avatar của group
       - Thêm/xóa participants
       - Promote/demote admin
       - Xóa conversation

3. **ParticipantsHash**:
   - DIRECT: Hash từ 2 user IDs (sorted)
   - GROUP: Hash từ tất cả user IDs (sorted)
   - Dùng để tìm conversation đã tồn tại
   - Đảm bảo không tạo duplicate conversation

4. **Validation**:
   - DIRECT: Tự động khi có 1 participant (tổng 2 người)
   - GROUP: Tự động khi có 2+ participants (tổng 3+ người)
   - Chỉ GROUP mới có thể update name/avatar, add/remove participants
   - Chỉ GROUP mới có thể promote/demote admin
   - DIRECT: Tất cả đều có quyền như nhau (vì đều là ADMIN)

## ✅ Kết luận

**Chat Service đã đầy đủ chức năng cho:**
- ✅ Chat 2 người (DIRECT) - Tự động xác định khi có 1 participant
- ✅ Group chat (GROUP) - Tự động xác định khi có 2+ participants
- ✅ Role-based permissions (ADMIN/MEMBER)
  - DIRECT: Tất cả đều là ADMIN
  - GROUP: Chỉ creator là ADMIN, có thể promote/demote
- ✅ Tất cả các API cần thiết
- ✅ WebSocket real-time
- ✅ Read receipts & unread count
- ✅ Security & validation

**Code đã được tối ưu với:**
- ✅ Tự động xác định loại conversation (DIRECT/GROUP)
- ✅ Role management thông minh (ADMIN chỉ áp dụng với GROUP)
- ✅ Quyền hạn rõ ràng cho từng loại conversation

