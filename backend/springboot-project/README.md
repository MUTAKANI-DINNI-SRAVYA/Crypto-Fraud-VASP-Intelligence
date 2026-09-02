# Spring Boot Backend: Crypto Fraud & VASP Intelligence

This is the backend service built with **Java 17** and **Spring Boot 3.2.x**.

---

## 🛠️ Tech Stack & Requirements
- **Java:** JDK 17 or higher
- **Build Tool:** Apache Maven 3.8+ (or your IDE's embedded Maven)
- **Port:** `8080` (Base URL: `http://localhost:8080/api`)

---

## 📂 Package Architecture & Team Responsibilities

```
src/main/java/com/cryptofraud/
├── CryptoFraudApplication.java     # Main Application Entrypoint & CORS (Member 1)
│
├── controller/                     # REST API Endpoints (Member 1)
│   ├── HealthController.java       # Operational Health Check (GET /api/health)
│   ├── WalletController.java       # (To be added on Day 2)
│   ├── RiskController.java         # (To be added on Day 2)
│   └── ReportController.java       # (To be added on Day 2)
│
├── model/                          # Shared Data Contract POJOs (ALL MEMBERS)
│   ├── Transaction.java            # Core transaction model
│   ├── WalletSummary.java          # Wallet aggregate statistics
│   ├── RiskResult.java             # Risk score & triggered rules
│   ├── FundFlowGraph.java          # Cytoscape nodes and edges
│   ├── VaspCheckResult.java        # VASP detection & Last Traceable Point flag
│   └── InvestigationReport.java    # Complete compiled dossier
│
└── service/                        # Business Logic Modules
    ├── blockchain/                 # Member 2: Etherscan client & mock loader
    ├── risk/                       # Member 3: Fraud heuristics & scoring rules
    ├── graph/                      # Member 4: Graph transformation algorithms
    ├── vasp/                       # Member 6: VASP catalog lookup & boundary flags
    └── report/                     # Member 6: AI prompt engine & dossier builder
```

---

## 🚀 How to Run the Backend Locally

### Option 1: Using Command Line (Maven)
```bash
# Navigate to this directory
cd backend/springboot-project

# Run the application
mvn spring-boot:run
```

### Option 2: Using Your IDE (IntelliJ IDEA, Eclipse, VSCode)
1. Open this folder (`backend/springboot-project`) in your IDE.
2. Ensure Project SDK is set to **Java 17**.
3. Locate `src/main/java/com/cryptofraud/CryptoFraudApplication.java`.
4. Right-click and choose **Run 'CryptoFraudApplication'**.

---

## 🧪 Verifying the Backend is Working
Once running, test the health check endpoint:

```bash
# Using curl:
curl http://localhost:8080/api/health

# Or open in your browser:
http://localhost:8080/api/health
```

**Expected JSON Response:**
```json
{
  "status": "UP",
  "application": "Crypto Fraud & VASP Intelligence",
  "mockModeEnabled": true,
  "version": "1.0.0-SNAPSHOT",
  "message": "Crypto Fraud & VASP Intelligence Backend is operational."
}
```

---

## 💡 Quick Tips for Teammates
1. **Mock Data First:** Always test your service methods using `data/sample-transactions.json` and `data/sample-vasps.json` before attempting external API calls.
2. **Never change the fields in `com.cryptofraud.model.*`** without consulting the Team Lead. All members rely on these exact names.
3. **Keep it simple:** Use standard Java collections (`List`, `Map`), simple loops, and clear `if/else` statements. No complicated design patterns or enterprise overhead needed!
