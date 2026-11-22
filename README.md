# FinTrack 💸

A full-stack **personal finance mobile app**. Track your income and expenses, see where
your money goes with charts, and keep it all private behind a secure login.

Built end-to-end by **Iheb Sellami** — mobile frontend, backend API, database, auth, and deployment.

---

## Tech stack

| Layer | Technology |
|-------|-----------|
| Mobile app | React Native + Expo (TypeScript) |
| Backend API | Java 25 + Spring Boot, Spring Data JPA |
| Database | PostgreSQL (run via Docker) |
| Auth & security | Spring Security + JWT |
| CI | GitHub Actions |
| Deployment | Docker + cloud host |

## Architecture

```
┌──────────────────────┐      HTTP/JSON      ┌───────────────────────┐      SQL      ┌──────────────┐
│  Mobile app (phone)  │ ──────────────────► │  Backend API (Spring) │ ────────────► │  PostgreSQL  │
│  React Native + Expo │ ◄────────────────── │  + JWT security        │ ◄──────────── │  database    │
└──────────────────────┘                     └───────────────────────┘               └──────────────┘
```

## Project structure

```
fintrack/
├── backend/    # Java + Spring Boot API
├── mobile/     # React Native + Expo app
├── docs/       # Design & architecture docs
└── docker-compose.yml   # starts the PostgreSQL database
```

## Status

🚧 In active development. See [`docs/DESIGN.md`](docs/DESIGN.md) for the full plan.
