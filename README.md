
# 🤖 AI Chatbot Backend

> Production-ready Spring Boot backend powering an AI-powered chatbot application using Google Gemini AI, JWT Authentication, Google OAuth2, PostgreSQL, Docker, and CI/CD.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

---

## 🌐 Live Services

- Backend API: https://chatbot-backend-a3su.onrender.com
- Swagger Docs: https://chatbot-backend-a3su.onrender.com/swagger-ui.html

---

# 🚀 Features

- 🔐 JWT Authentication + Refresh Tokens
- 🌍 Google OAuth2 Login
- 🤖 Google Gemini 3.0 Flash Preview AI Integration
- 💬 Persistent Chat Sessions
- 🗄️ PostgreSQL Database (Neon)
- 📄 Swagger/OpenAPI Documentation
- 🐳 Dockerized Deployment
- ⚙️ GitHub Actions CI/CD Pipeline
- 🧠 Chat Memory using Spring AI
- 🛡️ Spring Security 7
- 📦 Layered N-Tier Architecture

---

# 🛠️ Tech Stack

| Technology | Version  |
|------------|----------|
| Java | 21       |
| Spring Boot | 4.0.6    |
| Spring Security | 7.x      |
| Spring AI | 2.0.0-M5 |
| PostgreSQL | 17       |
| Maven | 3.x      |
| Docker | Latest   |
| MapStruct | 1.6.3    |
| JJWT | 0.12.6   |

---

# 🏛️ Architecture

```text
Angular Frontend
      │
      ▼
Spring Boot API
      │
      ▼
Service Layer
      │
      ▼
Repository Layer
      │
      ▼
PostgreSQL Database
```

---

# 📁 Project Structure

```text
src/main/java/com/sujan/chatbot/backend
├── config/
├── controller/
├── service/
├── repository/
├── model/
├── dto/
├── mapper/
├── exception/
└── enums/
```

---

# 🔐 Environment Variables

Create an `.env` or configure environment variables:

```env
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=your_database_url
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
GEMINI_API_KEY=your_gemini_api_key
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
FRONTEND_URL=https://your-frontend.vercel.app
```

---

# 💻 Local Development

## Prerequisites

- Java 21
- Maven
- Docker Desktop
- PostgreSQL

## Clone Repository

```bash
git clone https://github.com/sujankim/AI_ChatBot_Backend.git
cd AI_ChatBot_Backend
```

## Run Application

```bash
./mvnw spring-boot:run
```

Application runs at:

```text
http://localhost:8080
```

---

# 🐳 Docker

## Build Image

```bash
docker build -t chatbot-backend .
```

## Run Container

```bash
docker run -p 8080:8080 chatbot-backend
```

---

# 📡 API Endpoints

## Authentication

| Method | Endpoint |
|--------|----------|
| POST | /api/auth/register |
| POST | /api/auth/login |
| POST | /api/auth/refresh |
| POST | /api/auth/logout |

## Chats

| Method | Endpoint |
|--------|----------|
| GET | /api/chats |
| POST | /api/chats |
| DELETE | /api/chats/{id} |

## Messages

| Method | Endpoint |
|--------|----------|
| GET | /api/chats/{id}/messages |
| POST | /api/chats/{id}/messages |

---

# ⚙️ CI/CD Pipeline

GitHub Actions workflow:

```text
Push to main
   ↓
Run Tests
   ↓
Build JAR
   ↓
Build Docker Image
   ↓
Push to Docker Hub
   ↓
Deploy to Render
```


# 🧪 Testing

```bash
./mvnw test
```

Uses:

- JUnit 5
- Spring Boot Test
- H2 Database

---

# 📄 License

This project is licensed under the MIT License.

---

# 👨‍💻 Author

Built by Sujan using Spring Boot, Angular, PostgreSQL, Docker, and Gemini AI.
