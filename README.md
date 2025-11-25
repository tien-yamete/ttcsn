# Friendify - Social Network Website (Microservice Architecture)

## 📖 Introduction
**Friendify** is a social networking website designed with **Microservice Architecture**.  
This project is part of a university internship course, aiming to provide hands-on experience in developing both **Backend and Frontend** applications.

Goals of the project:
- Build a **mini social network** where users can register, authenticate, manage profiles, connect with friends, share posts, comment, and like.
- Learn to separate backend and frontend while applying modern software architecture patterns.
- Practice working with asynchronous communication, authentication, and scalable system design.

---

## 🏗️ System Architecture
The platform is structured into several microservices:

- **API Gateway** (Port 8080): Central entry point for all client requests, routing to appropriate microservices.
- **Config Server** (Port 8888): Centralized configuration management for all microservices.
- **Identity Service** (Port 8081): User registration, login, and authentication secured with Spring Security and JWT.
- **Profile Service** (Port 8082): Manage user profiles and account settings.
- **Notification Service** (Port 8083): Provide user notifications and system events via Kafka and SendGrid.
- **Post Service** (Port 8084): CRUD operations for posts with support for media attachments, saving, and sharing.
- **File Service** (Port 8085): Handle file uploads and media management integrated with Cloudinary.
- **Chat Service** (Port 8086): Real-time messaging and chat functionality with WebSocket support.
- **Social Service** (Port 8087): Manages friendships, follow/unfollow, and user blocking features.
- **Interaction Service** (Port 8088): Handles comments and likes/reactions on posts and comments.
- **Group Service** (Port 8089): Manages groups, group members, permissions, and group posts.

---

## 🛠️ Tech Stack
### Backend
- **Java 17 + Spring Boot 3.x** (Spring MVC, Data JPA, Security, WebSocket)
- **MySQL** as the primary database 
- **MongoDB** 
- **Apache Kafka** for asynchronous event-driven communication
- **Redis** for caching frequently accessed data
- **Maven** for build & dependency management

### Others
- **JWT Authentication** for secure stateless login
- **Swagger (Springdoc OpenAPI)** for API documentation
- **Docker & Docker Compose** for containerization and deployment
- **Lombok, MapStruct** to reduce boilerplate code
- **Cloudinary API** for media storage
- **SendGrid API** for email notifications

---

## 📂 Project Structure
```
microservice-social-network/
│── api-gateway/          # API Gateway service
│── config-server/        # Configuration server
│── identity-service/     # Authentication & authorization
│── profile-service/       # User profile management
│── notification-service/ # Notification handling
│── post-service/         # Post management
│── file-service/         # File upload & media management
│── chat-service/         # Real-time chat & messaging
│── social-service/       # Friendships, follow/unfollow, blocking
│── interaction-service/  # Comments and likes/reactions
│── group-service/        # Group management, members, permissions
│── shared-common/        # Shared common utilities
│── shared-contacts/      # Shared contact/friend utilities
│
└── README.md
```

---

## 🚀 Getting Started
### Prerequisites
- **Java 17+**
- **Maven**
- **Node.js 18+ and npm/yarn**
- **MySQL/PostgreSQL**
- **MongoDB** 
- **Redis**
- **Apache Kafka** 
- **Docker & Docker Compose (optional)**

### Local Setup
Clone repository:
```bash
git clone https://github.com/tien-yamete/friendify.git
cd friendify
```

**Important**: Start services in the following order:
1. **Config Server** (Port 8888) - Must be started first
2. **API Gateway** (Port 8080)
3. **Identity Service** (Port 8081) - Authentication required by other services
4. **Profile Service** (Port 8082)
5. Other microservices (Notification, Post, File, Chat, Social, Interaction, Group)

Configure environment (DB, Redis, JWT_SECRET, Cloudinary, Email) inside each service's:
```
src/main/resources/application.yaml
```

Build all services:
```bash
# From root directory
mvn clean install
```

Run a service (from the service directory):
```bash
cd <service-name>
mvn spring-boot:run
# or
java -jar target/<service-name>-0.0.1-SNAPSHOT.jar
```

Access Swagger UI (via API Gateway):
```
http://localhost:8080/swagger-ui.html
```

### Docker Setup
If Docker Compose is configured, simply run:
```bash
docker-compose up --build
```

---

## 📌 Features

### Core Services
- **API Gateway**: Centralized routing and load balancing for all microservices.
- **Identity & Authentication**: Secure login/registration with JWT and Spring Security, OTP verification, password reset.
- **User Management**: Profile updates, role-based access (Student, Instructor, Admin).
- **Profile Service**: User profile management, avatar/background upload, user search.

### Content & Social
- **Post Management**: Create, edit, delete, and list posts with text and media support. Privacy settings (PUBLIC, FRIENDS, PRIVATE), post saving/bookmarking, and sharing. Support for group posts.
- **Interaction Service**: Comments and likes/reactions on posts and comments. Nested replies support.
- **Social Service**: Friend requests, friendships management, follow/unfollow system, user blocking.
- **Group Service**: Create and manage groups, add/remove members, set permissions (admin, moderator, member), control posting permissions, moderation settings, and join requests.

### Communication
- **Real-time Chat**: WebSocket-based messaging with:
  - **Direct messaging (1-on-1 chat)**: Automatically created when chatting with 1 person
    - Both participants have equal admin rights
  - **Group chat**: Automatically created when chatting with 2+ people
    - Creator is admin, can promote/demote other admins
    - Admin-only features: update group info, add/remove members
  - **Auto-detection**: Conversation type (DIRECT/GROUP) is automatically determined by participant count
  - Read receipts and unread message counts
  - Typing indicators
  - Message edit/delete capabilities
- **Notifications**: Event-driven notifications via Kafka and email notifications via SendGrid.

### Media & Files
- **File Management**: File uploads and media management integrated with Cloudinary.

### Infrastructure
- **Caching**: Redis for performance improvement.
- **Configuration Management**: Centralized configuration via Spring Cloud Config Server.
- **Event-Driven Architecture**: Apache Kafka for asynchronous communication between services.
- **API Documentation**: Interactive Swagger UI for exploring APIs.

---

## 👨‍💻 Author
- Name: **Tạ Văn Tiến**

---

## 📜 License
This project is created for **educational purposes** only and is not intended for commercial use.
