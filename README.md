# NotifyMesh

**NotifyMesh** is a distributed event-driven notification platform designed to deliver notifications across multiple channels such as Email, Telegram, Slack, and Discord.

The system is built using a microservices architecture and leverages asynchronous messaging to ensure scalable and reliable notification delivery. NotifyMesh demonstrates how modern backend systems handle notification workflows in a decoupled and event-driven manner.

---

## 🚀 Overview

Modern applications need reliable and scalable ways to notify users. NotifyMesh provides a platform where applications can publish notification requests that are asynchronously processed and delivered through different communication channels.

The platform focuses on:

- Scalability  
- Service decoupling  
- Event-driven processing  
- Multi-channel notification delivery  

---

## 🏗 Architecture

NotifyMesh follows a microservices architecture where each service has a clear responsibility.

### Core Services

**Notification Service**

- Entry point for notification requests  
- Validates incoming requests  
- Publishes notification events  
- Manages scheduling and notification tracking  

**Template Service**

- Manages reusable notification templates  
- Provides APIs to create, update, and retrieve templates  
- Supports dynamic template rendering using variables  

**Distribution Service**

- Consumes notification events  
- Retrieves the required template  
- Renders messages using provided variables  
- Delivers notifications through different communication channels  

---

## 🔄 System Flow

1. Client sends a notification request  
2. Notification Service validates the request  
3. Notification Service publishes an event  
4. Distribution Service consumes the event  
5. Distribution Service retrieves the template  
6. Message is rendered with variables  
7. Notification is delivered through the selected channel  

---

## ✨ Features

- Multi-channel notification delivery  
- Event-driven architecture  
- Template-based message rendering  
- Attachment support (PDF/images)  
- Scheduled notifications  
- Asynchronous processing  
- Microservices architecture  
- Notification tracking and history  

---

## 🛠 Tech Stack

**Backend**

- Java  
- Spring Boot  

**Messaging**

- Apache Kafka  

**Databases**

- Relational Database  
- Document Database  

**Cloud Infrastructure**

- AWS EC2  
- AWS S3  
- AWS VPC  

---

## 📦 Project Structure

```
NotifyMesh
│
├── notification-service
│
├── template-service
│
├── distribution-service
│
├── architecture
│   ├── system-architecture.png
│   └── notification-flow.png
│
└── README.md
```

---

## 🎯 Project Goal

The goal of NotifyMesh is to demonstrate the design and implementation of a scalable distributed notification system using modern backend technologies such as microservices, event-driven messaging, and cloud infrastructure.

This project is built to explore system design concepts and real-world backend architecture patterns.

---

## 📌 Future Enhancements

- SMS and Push Notification support  
- User notification preferences  
- Retry and dead-letter queues  
- Rate limiting  
- Observability and monitoring  
- Containerization and orchestration  

---

## 📄 License

This project is licensed under the MIT License.
