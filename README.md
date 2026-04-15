# This is the general README file.

In this project, my goal is to build the very basics of a fitness app. Later on it should ascend beyond a basic CRUD-App, with functionalities likes "1 max rep prediction/calculation".

Looking at the tech stack, it is probably a bit overengineered, but the idea is to learn architecture, make design decisions, and understand how a potential web-app functions from top to bottom. Focus lies on the backend and database, but Next.js is used as a potential client to make us of that.

This project has nothing to do with my studies and is a side project I'm planning to maintain.

---

# Requirements

- Docker & Docker compose
- Java 21+
- Node.js 24+
- Maven (optional, wrapper included)

# Tech Stack

- Oracle Database 
- Spring Boot 4.x
- Next.js 16.x

---

# Setup Guide 

(Reference Point: 15.04.2025 and prior)

## Overview: 
- Oracle Database
- Spring Backend
- Next.js Frontend

--

## Oracle Database:
- Configure Docker Compose by adding secrets based on: db/.env.example.
- The database user "fitness_user" is created and used by the application.

--

## Spring Backend:
- The backend is managed by IntelliJ.
- Database access is configured using environment variables inside IntelliJ.
- The application is automatically built and run via IntelliJ runtime.

Note: Dependency changes might require **manual Maven reload**.

--

## Nextjs:
- Run the development server using: npm run dev



