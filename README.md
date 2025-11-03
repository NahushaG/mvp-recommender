# MVP Recommender ⚽

A **Fantasy Premier League (FPL)** application that recommends MVP (Most Valuable Player) picks using AI insights and live football data.

This project is a **monorepo** containing both the backend (Spring Boot) and frontend (React) services.

---

## 📁 Project Structure

```
mvp-recommender/
├── backend/   # Spring Boot (Gradle) REST API service
├── frontend/  # React frontend for displaying player stats & recommendations
└── README.md  # This file
```

---

## 🧠 Overview

- **Backend** – Spring Boot REST API providing player stats, fixtures, and AI-powered MVP recommendations.  
- **Frontend** – React-based UI that visualizes recommendations and allows users to explore FPL data.

---

## 🚀 Quick Start

### Clone the repository
```bash
git clone https://github.com/NahushaG/mvp-recommender.git
cd mvp-recommender
```

### Backend setup
```bash
cd backend
cp .env.example .env   # Add your OPENAI_API_KEY and DB credentials
./gradlew bootRun
```

### Frontend setup
```bash
cd frontend
npm install
npm start
```
Open [http://localhost:3000](http://localhost:3000) to view the app.

---

## ⚙️ Environment Variables
Both backend and frontend use `.env` files for configuration.

Example for backend `.env`:
```dotenv
OPENAI_API_KEY=your_openai_api_key
DB_USER=your_db_user
DB_PASSWORD=your_db_password
```

---

## 🧩 Tech Stack
- **Backend**: Java 17, Spring Boot, Gradle, PostgreSQL (or H2)  
- **Frontend**: React, TypeScript, TailwindCSS  
- **AI Integration**: OpenAI API (via environment variable)  

---

## 📖 Additional Documentation
- [backend/README.md](backend/README.md) – detailed setup and API documentation  
- [frontend/README.md](frontend/README.md) – UI development and build instructions

---

👤 Author
Nahusha G GitHub: NahushaG
