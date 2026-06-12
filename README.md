# AI-Powered Fitness Tracker (Microservices)

An event-driven, AI-powered fitness tracking platform built with a Spring Boot microservices architecture and a modern React frontend. This application allows users to securely log in, track their fitness activities, and receive personalized, AI-generated fitness advice using Google's Gemini AI.

## 🚀 Architecture Overview

The backend is built as a distributed microservices system to ensure scalability, fault tolerance, and separation of concerns.

- **API Gateway (`gateway`)**: The entry point for all frontend requests. It handles routing using Spring Cloud Gateway, manages CORS, and acts as an OAuth2 Resource Server. It intercepts Keycloak JWT tokens and synchronizes new users into the local database automatically.
- **Service Discovery (`eureka`)**: Netflix Eureka server for dynamic service registration and client-side load balancing.
- **Config Server (`configServer`)**: Centralized configuration management for all microservices.
- **User Service (`userService`)**: Manages user identities and profiles. Backed by a **MySQL** database.
- **Activity Service (`activityService`)**: Manages fitness activities (duration, type, calories burned). When a new activity is saved, it publishes an event to RabbitMQ.
- **AI Service (`aiService`)**: An asynchronous worker service that listens to the RabbitMQ `activity.queue`. When triggered, it calls the Gemini AI API to generate personalized feedback, safety tips, and next steps. The results are stored in a **MongoDB** database.

## 💻 Tech Stack

### Backend
- **Java 17** & **Spring Boot 3**
- **Spring Cloud** (Gateway, Netflix Eureka, Config)
- **Spring Security** (OAuth2 / OIDC)
- **Messaging**: RabbitMQ
- **Databases**: MySQL (Relational), MongoDB (NoSQL)
- **AI Integration**: Google Gemini AI API

### Frontend
- **React.js** (built with Vite)
- **Styling**: Vanilla CSS with a custom "Glassmorphism" design system
- **Authentication**: `react-oidc-context` (Keycloak Integration)
- **Icons**: Lucide React
- **HTTP Client**: Axios

## 🛠️ Prerequisites

To run this project locally, ensure you have the following installed:
- **Java 17+**
- **Node.js 18+** & **npm**
- **Maven**
- **MySQL** running locally (or via Docker)
- **MongoDB** running locally (or via Docker)
- **RabbitMQ** running locally (or via Docker)
- **Keycloak** running locally (port `8181` is expected by default)

## 🔑 Keycloak Setup

1. Start your local Keycloak server.
2. Create a new Realm named `fitness-oauth2`.
3. Create an OIDC client named `fitness-react-client`.
   - Set **Valid Redirect URIs** to `http://localhost:5173/*`
   - Set **Web Origins** to `http://localhost:5173`
   - Ensure "Standard Flow" and "Implicit Flow" are enabled.
4. Enable **User Registration** in your Realm settings so new users can sign up from the React login page.

## 🚀 Getting Started

### 1. Start the Infrastructure Services
It is crucial to start the infrastructure services first in this exact order:
1. **Config Server** (`configServer`): `mvn spring-boot:run`
2. **Eureka Server** (`eureka`): `mvn spring-boot:run`
3. **API Gateway** (`gateway`): `mvn spring-boot:run`

### 2. Start the Business Microservices
1. **User Service** (`userService`): `mvn spring-boot:run`
2. **Activity Service** (`activityService`): `mvn spring-boot:run`
3. **AI Service** (`aiService`): `mvn spring-boot:run` *(Ensure you have added your Gemini API key to the config server properties!)*

### 3. Start the React Frontend
Navigate to the `frontend` directory:
```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`. 
The API Gateway runs on `http://localhost:1010` and routes traffic seamlessly to the backend services.

## ✨ Features

- **Secure Single Sign-On (SSO):** Users log in directly through Keycloak.
- **Auto User Synchronization:** The Gateway intercepts first-time logins and automatically persists user data to the MySQL database without manual intervention.
- **Dynamic Routing:** API calls are dynamically routed to healthy service instances using Eureka and Reactive Load Balancing.
- **Asynchronous AI Recommendations:** Logging an activity sends an AMQP message to the AI service, preventing the UI from freezing while waiting for Gemini to generate detailed fitness advice.
- **Glassmorphism UI:** A sleek, premium, and highly responsive frontend experience.
