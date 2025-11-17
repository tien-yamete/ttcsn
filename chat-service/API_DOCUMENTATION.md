# Chat Service API Documentation

## Base URL
- **API Gateway**: `http://localhost:8080/api/v1/chat`
- **Direct Service**: `http://localhost:8086` (nếu test trực tiếp)

## Authentication
Tất cả API đều yêu cầu JWT token trong header:
```
Authorization: Bearer <your_jwt_token>
```

---

## 1. Conversation APIs

### 1.1. Tạo Conversation
**POST** `/conversations/create`

**Request Body:**
```json
{
  "typeConversation": "DIRECT",
  "participantIds": ["user-id-1", "user-id-2"]
}
```

**Response:**
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "id": "conversation-id",
    "typeConversation": "DIRECT",
    "participantsHash": "user-id-1_user-id-2",
    "conversationAvatar": "avatar-url",
    "conversationName": "username",
    "participants": [
      {
        "userId": "user-id-1",
        "username": "user1",
        "firstName": "First",
        "lastName": "Last",
        "avatar": "avatar-url"
      }
    ],
    "createdDate": "2024-01-01T00:00:00Z",
    "modifiedDate": "2024-01-01T00:00:00Z"
  }
}
```

**TypeConversation values:**
- `DIRECT` - Chat 1-1
- `GROUP` - Chat nhóm

---

### 1.2. Lấy danh sách Conversation của user
**GET** `/conversations/my-conversations`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "code": 1000,
  "message": null,
  "result": [
    {
      "id": "conversation-id-1",
      "typeConversation": "DIRECT",
      "participantsHash": "user-id-1_user-id-2",
      "conversationAvatar": "avatar-url",
      "conversationName": "username",
      "participants": [...],
      "createdDate": "2024-01-01T00:00:00Z",
      "modifiedDate": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

## 2. Chat Message APIs

### 2.1. Tạo Chat Message
**POST** `/messages/create`

**Request Body:**
```json
{
  "conversationId": "conversation-id",
  "message": "Hello, this is a test message"
}
```

**Response:**
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "id": "message-id",
    "conversationId": "conversation-id",
    "me": true,
    "message": "Hello, this is a test message",
    "sender": {
      "userId": "user-id",
      "username": "username",
      "firstName": "First",
      "lastName": "Last",
      "avatar": "avatar-url"
    },
    "createdDate": "2024-01-01T00:00:00Z"
  }
}
```

---

### 2.2. Lấy danh sách Messages trong Conversation
**GET** `/messages?conversationId={conversationId}`

**Query Parameters:**
- `conversationId` (required): ID của conversation

**Response:**
```json
{
  "code": 1000,
  "message": null,
  "result": [
    {
      "id": "message-id-1",
      "conversationId": "conversation-id",
      "me": true,
      "message": "Message 1",
      "sender": {...},
      "createdDate": "2024-01-01T00:00:00Z"
    },
    {
      "id": "message-id-2",
      "conversationId": "conversation-id",
      "me": false,
      "message": "Message 2",
      "sender": {...},
      "createdDate": "2024-01-01T00:01:00Z"
    }
  ]
}
```

---

## 3. WebSocket APIs

### 3.1. WebSocket Connection
**WebSocket URL**: `ws://localhost:8086/ws` hoặc `ws://localhost:8080/ws` (qua gateway)

**SockJS URL**: `http://localhost:8086/ws` hoặc `http://localhost:8080/ws`

**Connection Headers:**
```
Authorization: Bearer <your_jwt_token>
```

### 3.2. Send Message via WebSocket
**Destination**: `/app/chat.sendMessage`

**Message Body:**
```json
{
  "conversationId": "conversation-id",
  "message": "Hello via WebSocket"
}
```

**Subscribe to receive messages:**
- **Destination**: `/topic/conversation/{conversationId}`

---

### 3.3. Typing Indicator
**Destination**: `/app/chat.typing`

**Message Body:**
```json
{
  "userId": "user-id",
  "conversationId": "conversation-id",
  "isTyping": true
}
```

**Subscribe to typing events:**
- **Destination**: `/topic/conversation/{conversationId}/typing`

---

### 3.4. User Join/Leave Notification
**Destination**: `/app/chat.addUser`

**Message Body:**
```json
{
  "conversationId": "conversation-id",
  "sender": "user-id",
  "content": "User joined",
  "type": "JOIN"
}
```

**Type values:**
- `JOIN` - User tham gia
- `LEAVE` - User rời khỏi
- `TYPING` - User đang gõ

**Subscribe to notifications:**
- **Destination**: `/topic/conversation/{conversationId}`

---

## Error Responses

### 400 Bad Request
```json
{
  "code": 1001,
  "message": "Uncategorized error",
  "result": null
}
```

### 401 Unauthorized
```json
{
  "code": 1006,
  "message": "Unauthenticated",
  "result": null
}
```

### 403 Forbidden
```json
{
  "code": 1007,
  "message": "You do not have permission",
  "result": null
}
```

### 404 Not Found
```json
{
  "code": 1005,
  "message": "User not existed",
  "result": null
}
```

hoặc

```json
{
  "code": 1009,
  "message": "Chat conversation not found",
  "result": null
}
```

---

## Postman Collection

Import file `Chat_Service.postman_collection.json` vào Postman để test nhanh các API.

## Testing Flow

1. **Đăng nhập** để lấy JWT token (từ Identity Service)
2. **Tạo Conversation** với user khác
3. **Gửi Message** qua REST API hoặc WebSocket
4. **Lấy danh sách Messages** để xem lịch sử chat
5. **Test WebSocket** để test real-time messaging

