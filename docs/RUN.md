# Running FinTrack

Two parts:
- **Part A** — run everything on your computer and see the app on your phone (free, ~15 min).
- **Part B** — deploy the backend to the cloud so the app works without your laptop (optional).

---

## Prerequisites (install once on your computer)

| Tool | Why | Check it's installed |
|------|-----|----------------------|
| **Git** | clone the repo | `git --version` |
| **Java 21+** (JDK) | run the backend | `java -version` |
| **Docker Desktop** | run PostgreSQL | `docker --version` |
| **Node.js 20+** | run the mobile tooling | `node -v` |
| **Expo Go** app | open the app on your phone | install from App Store / Play Store |

Your phone and your computer must be on the **same Wi-Fi network**.

---

## Part A — Run locally & see it on your phone

### 1. Clone the project
```bash
git clone https://github.com/Siheb5/fintrack.git
cd fintrack
```

### 2. Start the database
```bash
docker compose up -d
docker compose ps        # should show fintrack-db as healthy
```

### 3. Start the backend
In a new terminal, from the project root:
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=postgres
```
Wait for `Started FintrackApplication`. Then verify:
```bash
curl http://localhost:8080/api/health     # -> {"status":"UP","service":"fintrack-backend"}
```
Leave this terminal running.

> No Docker? You can skip steps 2 and run the backend with plain `mvn spring-boot:run`
> (it uses an in-memory H2 database — data resets on restart, but perfect for a quick demo).

### 4. Find your computer's LAN IP
- **macOS / Linux:** `ifconfig | grep "inet "` → look for `192.168.x.x`
- **Windows:** `ipconfig` → look for `IPv4 Address`

Example result: `192.168.1.50`

### 5. Point the app at your backend
Edit `mobile/src/config.ts` and set the IP from step 4:
```ts
export const API_BASE_URL = 'http://192.168.1.50:8080';   // <-- your IP here
```
(Use `http://`, your IP, and port `8080`. Do **not** use `localhost` — on the phone that means the phone itself.)

### 6. Start the mobile app
In a third terminal, from the project root:
```bash
cd mobile
npm install
npx expo start
```
A QR code appears in the terminal.

### 7. Open it on your phone
- Open **Expo Go** on your phone.
- Scan the QR code (Android: scan in Expo Go; iPhone: scan with the Camera app, it opens Expo Go).
- FinTrack loads on your phone. 🎉

### 8. Use it
- Tap **Create one** → register (email + password of 8+ chars).
- You land on the dashboard. Tap **+** to add income/expenses.
- Watch the balance and the spending chart update.

### Troubleshooting
- **"Could not load data"** in the app → the phone can't reach the backend. Re-check the IP
  in `config.ts`, confirm backend is running, and that phone + computer share one Wi-Fi.
  A firewall may block port 8080 — allow it, or use Part B (cloud).
- **QR won't connect** → try `npx expo start --tunnel` (routes over the internet instead of LAN).
- **Backend won't start on 8080** → something else uses that port; stop it, or change the port.

---

## Part B — Deploy the backend to the cloud (optional)

This lets the app work from anywhere, without your laptop running. Free tiers exist on
**Render**, **Railway**, and **Fly.io**. General outline (Render shown):

1. Push is already on GitHub, so connect your repo to the host.
2. Create a **managed PostgreSQL** instance on the host; copy its connection URL,
   username, and password.
3. Create a **Web Service** from the `backend/` folder. Set these environment variables:
   - `SPRING_PROFILES_ACTIVE=postgres`
   - `DB_URL=jdbc:postgresql://<host>:<port>/<database>`
   - `DB_USERNAME=<user>`
   - `DB_PASSWORD=<password>`
   - `JWT_SECRET=<a long random string, 32+ characters>`
4. Build command: `./mvnw -B package -DskipTests` (or `mvn -B package -DskipTests`).
   Start command: `java -jar target/*.jar`.
5. Once deployed, you get a public URL like `https://fintrack-api.onrender.com`.
   Put that (with `https://`, no port) in `mobile/src/config.ts` as `API_BASE_URL`.

Now the app talks to your cloud backend from any network.

> A production `Dockerfile` for the backend can be added later to make cloud deploys
> even simpler; the profile and env-var setup above already supports it.
