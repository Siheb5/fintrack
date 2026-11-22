# FinTrack — Design

## 1. What it is

FinTrack is a mobile app where a user creates an account, logs in, and tracks their money.
They add income and expenses (amount, category, date), and see charts of where their money
goes. Every piece of data is private to its owner — the backend enforces that you only ever
see your own transactions.

## 2. Features

### MVP (build first)
- Register a new account
- Log in / log out
- Add / edit / delete a transaction
- List my transactions
- See my total balance
- Spending-by-category chart

### Later (nice-to-have)
- Budgets with "80% spent" alerts
- Monthly trend charts
- CSV bank-statement import (with data-quality checks)
- Recurring transactions
- AI-based spending insights

## 3. Screens

```
[Login] ──register──> [Register]
   │ login success (receive JWT token)
   ▼
[Home / Dashboard]  — total balance + spending-by-category chart
   │        └──> [Add Transaction]  (amount, category, date, note)
   ├──> [Transactions List] ──tap──> [Transaction Detail / Edit]
   └──> [Profile]  (logout)
```

## 4. Data model

One USER has many TRANSACTIONs. Each transaction belongs to exactly one user
(via the `user_id` foreign key), which is how privacy is enforced.

### USER
| column        | type      | notes                        |
|---------------|-----------|------------------------------|
| id            | bigint    | primary key                  |
| email         | varchar   | unique, used to log in       |
| password_hash | varchar   | never store the raw password |
| created_at    | timestamp |                              |

### TRANSACTION
| column       | type      | notes                                 |
|--------------|-----------|---------------------------------------|
| id           | bigint    | primary key                           |
| user_id      | bigint    | foreign key → USER.id (the owner)     |
| amount       | numeric   | money, always positive                |
| type         | varchar   | INCOME or EXPENSE                     |
| category     | varchar   | e.g. Food, Rent, Salary               |
| note         | varchar   | optional free text                    |
| occurred_on  | date      | when the money moved                  |
| created_at   | timestamp |                                       |

## 5. API endpoints

| Method | Path                           | Purpose                          | Auth |
|--------|--------------------------------|----------------------------------|:----:|
| POST   | /api/auth/register             | Create a new user                | No   |
| POST   | /api/auth/login                | Log in, receive a JWT token      | No   |
| GET    | /api/transactions              | List my transactions             | Yes  |
| POST   | /api/transactions              | Add a transaction                | Yes  |
| GET    | /api/transactions/{id}         | Get one transaction              | Yes  |
| PUT    | /api/transactions/{id}         | Edit a transaction               | Yes  |
| DELETE | /api/transactions/{id}         | Delete a transaction             | Yes  |
| GET    | /api/transactions/summary      | Totals + per-category sums       | Yes  |

HTTP verbs = CRUD: POST=create, GET=read, PUT=update, DELETE=delete.
