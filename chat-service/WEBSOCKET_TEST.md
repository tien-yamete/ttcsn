# WebSocket Testing Guide

## WebSocket Connection

### 1. Sử dụng Postman WebSocket

1. Mở Postman
2. Tạo request mới, chọn **WebSocket**
3. URL: `ws://localhost:8086/ws` hoặc `ws://localhost:8080/ws` (qua gateway)
4. Headers:
   ```
   Authorization: Bearer <your_jwt_token>
   ```

### 2. Sử dụng JavaScript Client

```html
<!DOCTYPE html>
<html>
<head>
    <title>Chat WebSocket Test</title>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2/lib/stomp.min.js"></script>
</head>
<body>
    <h1>Chat WebSocket Test</h1>
    <div id="messages"></div>
    <input type="text" id="messageInput" placeholder="Type message...">
    <input type="text" id="conversationId" placeholder="Conversation ID">
    <button onclick="sendMessage()">Send</button>
    <button onclick="connect()">Connect</button>

    <script>
        let stompClient = null;
        const token = 'YOUR_JWT_TOKEN_HERE';
        const conversationId = 'YOUR_CONVERSATION_ID';

        function connect() {
            const socket = new SockJS('http://localhost:8086/ws');
            stompClient = Stomp.over(socket);
            
            stompClient.connect({
                'Authorization': 'Bearer ' + token
            }, function(frame) {
                console.log('Connected: ' + frame);
                
                // Subscribe to conversation messages
                stompClient.subscribe('/topic/conversation/' + conversationId, function(message) {
                    const data = JSON.parse(message.body);
                    displayMessage(data);
                });
                
                // Subscribe to typing indicators
                stompClient.subscribe('/topic/conversation/' + conversationId + '/typing', function(typing) {
                    const data = JSON.parse(typing.body);
                    console.log('Typing:', data);
                });
            });
        }

        function sendMessage() {
            const message = document.getElementById('messageInput').value;
            const convId = document.getElementById('conversationId').value;
            
            if (stompClient && message && convId) {
                stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
                    conversationId: convId,
                    message: message
                }));
                document.getElementById('messageInput').value = '';
            }
        }

        function displayMessage(message) {
            const messagesDiv = document.getElementById('messages');
            const messageElement = document.createElement('div');
            messageElement.innerHTML = `
                <strong>${message.sender.username}:</strong> ${message.message}
                <small>${new Date(message.createdDate).toLocaleString()}</small>
            `;
            messagesDiv.appendChild(messageElement);
        }
    </script>
</body>
</html>
```

### 3. Sử dụng cURL (cho testing)

```bash
# Test WebSocket connection (cần tool hỗ trợ WebSocket)
# Hoặc sử dụng wscat:
# npm install -g wscat
# wscat -c ws://localhost:8086/ws -H "Authorization: Bearer YOUR_TOKEN"
```

## WebSocket Endpoints

### Send Message
**Destination**: `/app/chat.sendMessage`

**Message:**
```json
{
  "conversationId": "conversation-id",
  "message": "Hello via WebSocket"
}
```

**Subscribe:** `/topic/conversation/{conversationId}`

---

### Typing Indicator
**Destination**: `/app/chat.typing`

**Message:**
```json
{
  "userId": "user-id",
  "conversationId": "conversation-id",
  "isTyping": true
}
```

**Subscribe:** `/topic/conversation/{conversationId}/typing`

---

### User Join/Leave
**Destination**: `/app/chat.addUser`

**Message:**
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

**Subscribe:** `/topic/conversation/{conversationId}`

## Testing Steps

1. **Connect WebSocket** với JWT token
2. **Subscribe** vào conversation topic: `/topic/conversation/{conversationId}`
3. **Send message** qua `/app/chat.sendMessage`
4. **Receive message** từ subscription
5. **Test typing indicator** qua `/app/chat.typing`

