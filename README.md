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

- **Identity Service**: User registration, login, and authentication secured with Spring Security and JWT.
- **User Service**: Manage user profiles and account settings.
- **Post Service**: CRUD operations for posts with support for media attachments.
- **Comment Service**: Manage comments and likes on posts.
- **Friend Service**: Handle friend requests, connections, and friend lists.
- **Notification Service**: Provide user notifications and system events.
- **Frontend (React.js)**: User interface for accessing and interacting with the system.

---

## 🛠️ Tech Stack
### Backend
- **Java 17 + Spring Boot 3.x** (Spring MVC, Data JPA, Security, WebSocket)
- **MySQL / PostgreSQL** as the primary database
- **Apache Kafka** for asynchronous event-driven communication
- **Redis** for caching frequently accessed data
- **Maven** for build & dependency management

### Frontend
- **React.js** for building the UI
- **TailwindCSS / Bootstrap** for responsive styling

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
│── backend/
│   ├── identity-service/
│   ├── user-service/
│   ├── post-service/
│   ├── comment-service/
│   ├── friend-service/
│   ├── notification-service/
│
│── frontend/
│   └── friendify-web/
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
- **Redis**
- **Docker & Docker Compose (optional)**

### Local Setup
Clone repository:
```bash
git clone https://github.com/tien-yamete/friendify.git
cd friendify
```

Configure environment (DB, Redis, JWT_SECRET, Cloudinary, Email) inside:
```
src/main/resources/application.yaml
```

Build the backend:
```bash
mvn clean install
```

Run a service:
```bash
mvn spring-boot:run
# or
java -jar target/friendify-0.0.1-SNAPSHOT.jar
```

Run the frontend:
```bash
cd frontend/friendify-web
npm install
npm start
```

Access Swagger UI:
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
- **Identity & Authentication**: Secure login/registration with JWT and Spring Security.
- **User Management**: Profile updates, role-based access (Student, Instructor, Admin).
- **Post Management**: Create, edit, delete, and list posts with text and media support.
- **Comment & Like**: User engagement through comments and likes.
- **Friendship System**: Friend requests, approvals, and friend list management.
- **Notifications**: Event-driven notifications for interactions.
- **File Uploads**: Integrated with Cloudinary for media uploads.
- **Email Service**: Account verification and password reset via SendGrid.
- **Caching**: Redis for performance improvement.
- **API Documentation**: Interactive Swagger UI for exploring APIs.

---

## 👨‍💻 Author
- Name: **[Your Name]**
- Class: **[Your Class]**
- Course: **Internship in Backend & Frontend Development**
- Supervisor: **[Lecturer’s Name]**

---

## 📜 License
This project is created for **educational purposes** only and is not intended for commercial use.
