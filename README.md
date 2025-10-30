# 🏆 MVP Recommender

A real-world project that uses AI prompts to analyze and recommend the best player choices for Fantasy Premier League (FPL).

---

## 🧠 What is it?

MVP Recommender is a backend service that:

* Fetches Fantasy Premier League (FPL) data (players, fixtures, stats)
* Uses an AI (OpenAI GPT-based) prompt engine to analyze players per position
* Generates recommendations for “Most Valuable Players” (MVPs)
* Can also build budgeted squads or other predictions

It combines domain logic, data sources, and AI-powered insights into a cohesive system.

---

## 📂 Project Structure

```
mvp-recommender/
├── gradle/
├── src/
│   ├── main/
│   │   ├── groovy/ (or java) – application logic, controllers, services
│   │   └── resources/ – config, application.yml, etc.
├── .gitignore
├── build.gradle
├── settings.gradle
├── gradlew & gradlew.bat
└── .env_samplefile ← sample env for secrets
```

**Key modules:**

* **Controller layer** — exposes REST endpoints (e.g., `/api/v1/fpl/squad/generate`)
* **Service layer** — handles business logic: fetching FPL data, AI prompt analysis, squad generation
* **Model / DTOs** — domain objects (Player, SquadRequest, Recommendation, etc.)
* **Configuration** — for database, OpenAI, HTTP clients, etc.

---

## 🚀 Getting Started

### Prerequisites

* Java 11+
* PostgreSQL (or another database)
* OpenAI API key (or alternative AI endpoint)

### Setup Steps

1. Clone the repository:

```bash
git clone https://github.com/NahushaG/mvp-recommender.git
cd mvp-recommender
```

2. Create an environment file:

```bash
cp .env_samplefile .env
```

Fill in the required variables:

* `DB_USER`, `DB_PASSWORD`, `DB_NAME`
* `OPENAI_API_KEY`
* Other environment variables as needed

3. Build and run:

```bash
./gradlew build
./gradlew bootRun
```

Or run via your IDE (Spring Boot / Grails / etc).

4. Access API endpoints using curl or Postman, for example:

```
POST /api/v1/fpl/squad/generate
```

---

## 🛠️ Configuration

Environment variables used:

* `DB_USER`
* `DB_PASSWORD`
* `DB_NAME`
* `OPENAI_API_KEY`
* `MVP_AI_MODEL`
* `MVP_AI_MAX_TOKENS`
* `MVP_AI_TEMPERATURE`
* `SENDERS_EMAIL_ID`
* `FPL_API_BASE_URL`

> Ensure your actual `.env` file is ignored by Git using `.gitignore`.

---

## 💡 Features

* MVP recommendations per position (Defender, Midfielder, Forward, Goalkeeper)
* Budget-based squad generation
* Integration with external APIs (FPL, OpenAI)
* Email notifications (optional)
* Extendable architecture for new algorithms, models, or data sources

---

## ✅ Usage Examples

### Budget Squad Request

```json
{
  "budget": 100.0,
  "formation": "3-4-3",
  "mustHavePlayers": [5, 82, 430],
  "excludedPlayers": [178, 256]
}
```

Expected response includes:

* A recommended squad (players per position)
* Total cost
* AI-generated analysis / insights

### Top MVP Recommendations

```
GET /api/v1/fpl/mvp/top
```

---

## 🎯 Future Improvements / Roadmap
* Test coverage: Add comprehensive test cases to capture various implementation and integration scenarios
* Frontend project: Build a user-friendly frontend (Preferred language: React) to showcase results
* AI improvements: Enhance AI prompt logic for deeper analysis and scenario-based suggestions
* User customization: Include user-specific preferences (leagues, scoring systems)
* API extensions: Add endpoints to fetch all players, filter players by attributes such as team, position, etc.
* Authorization & accounts: Implement user authentication and accounts
* Production-grade features: Include API gateway, caching, logging, tracking, and other non-functional requirements

---

## 🧾 License & Contribution

Feel free to fork or contribute. Open an issue or PR for suggestions or fixes.

---

## 👤 Author

**Nahusha G**
GitHub: [NahushaG](https://github.com/NahushaG)
