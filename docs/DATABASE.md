# FinTrack — Database

## Engine

- **Production / real use:** PostgreSQL 16, run in a Docker container (see `docker-compose.yml`).
- **Automated tests:** H2 in-memory database (fast, no setup, wiped after each run).

Same application code runs against both — only the connection settings differ. This is a
common professional pattern: tests use a disposable database, production uses PostgreSQL.

## Running the database locally

```bash
# from the project root
docker compose up -d      # start PostgreSQL in the background
docker compose ps         # check it is healthy
docker compose down       # stop it (data is kept in a named volume)
docker compose down -v    # stop it AND wipe all data
```

Connection details (local dev):

| Setting  | Value       |
|----------|-------------|
| Host     | localhost   |
| Port     | 5432        |
| Database | fintrack    |
| User     | fintrack    |
| Password | fintrack    |

## Schema

The schema is created by **Flyway migrations** in
`backend/src/main/resources/db/migration/`. Flyway runs each `V#__*.sql` file once,
in order, when the backend starts. The database structure is therefore version-controlled
alongside the code.

### Tables

**users** — one row per account
| column        | type         | notes                          |
|---------------|--------------|--------------------------------|
| id            | BIGSERIAL PK | auto-incrementing id           |
| email         | VARCHAR UNIQUE | login identifier             |
| password_hash | VARCHAR      | hashed password, never plain   |
| created_at    | TIMESTAMPTZ  | account creation time          |

**transactions** — one row per income/expense, owned by a user
| column       | type          | notes                                   |
|--------------|---------------|-----------------------------------------|
| id           | BIGSERIAL PK  | auto-incrementing id                    |
| user_id      | BIGINT FK     | → users.id, ON DELETE CASCADE           |
| amount       | NUMERIC(12,2) | positive money value                    |
| type         | VARCHAR       | INCOME or EXPENSE                       |
| category     | VARCHAR       | e.g. Food, Rent, Salary                 |
| note         | VARCHAR       | optional                                |
| occurred_on  | DATE          | date the money moved                    |
| created_at   | TIMESTAMPTZ   | row creation time                       |

A foreign key ties every transaction to its owner; an index on `user_id` keeps the
"list my transactions" query fast.
