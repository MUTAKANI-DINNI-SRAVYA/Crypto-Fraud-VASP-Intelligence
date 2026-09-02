# Crypto Fraud & VASP Intelligence 🕵️‍♂️⛓️

> **Hackathon Date:** September 7, 2026  
> **Project Type:** College Hackathon Prototype (6-Member Collaborative Project)  
> **Tech Stack:** Java 17 / Spring Boot (Backend) + React.js / Cytoscape.js (Frontend) + Ethereum / Etherscan API

---

## 📌 Executive Summary

**Crypto Fraud & VASP Intelligence** is an investigator-friendly prototype designed to help security researchers and analysts track suspicious cryptocurrency fund movement across Ethereum wallets.

The system ingests blockchain transactions, traces multi-hop layering, calculates an explainable heuristic risk score, visualizes fund flows in an interactive graph, detects Virtual Asset Service Provider (VASP) interactions, and generates an AI-assisted investigation briefing.

---

## ⚠️ Important Limitations & Ethical Disclaimer

> [!IMPORTANT]
> **This platform is an INVESTIGATION ASSISTANT, not a court-admissible forensic tool or automated AML compliance judge.**
>
> 1. **On-Chain Boundaries:** Public blockchain records reveal wallet-to-wallet transactions only. Blockchain data **cannot** reveal real-world identity, customer KYC records, bank accounts, or off-chain cash withdrawals.
> 2. **Last Traceable Point:** When funds enter a centralized exchange or custodial service (VASP), the blockchain trail terminates. The system explicitly tags this boundary as:
>    ```
>    [LAST TRACEABLE POINT] - Further lawful off-chain records required.
>    ```
> 3. **Heuristic Risk Scores:** Risk scores (0–100) are prototype heuristics based on observable transaction patterns (e.g., rapid splitting, multi-hop layering). They **must not** be used to declare any person, wallet, or entity criminal.
> 4. **Fictional Entity Names:** All sample VASP names (e.g., *ApexExchange*, *NovaPay Crypto*, *CoinHarbor*) are purely fictional demo identifiers.

---

## 👥 Six-Member Team Roles & Responsibilities

| Role | Member | Primary Focus | Feature Branch |
| :--- | :--- | :--- | :--- |
| **Team Lead / Integrator** | **Member 1** | Architecture, API contracts, Git workflow, code review, end-to-end integration | `feature/integration` |
| **Blockchain / Data Engineer** | **Member 2** | Etherscan public API client, wallet transaction loader, data models | `feature/blockchain` |
| **Fraud / Risk Engineer** | **Member 3** | Suspicious pattern detection (splitting, rapid hops, reconsolidation), risk score | `feature/risk-engine` |
| **Money Flow Graph Engineer** | **Member 4** | Graph generation (nodes & edges for Cytoscape.js / React Flow), flow layout | `feature/graph` |
| **Frontend / Dashboard Engineer** | **Member 5** | React dashboard, wallet search, risk cards, transaction table, VASP display | `feature/frontend` |
| **VASP + AI + Report Engineer** | **Member 6** | VASP catalog, "Last Traceable Point" tag, AI investigation explanation, report export | `feature/ai-vasp` |

---

## 🏛️ High-Level System Architecture

```
                       [ User Input: Wallet Address ]
                                     │
                                     ▼
                     [ Blockchain Data Retrieval ]
                    (Etherscan API / Local Mock JSON)
                                     │
                                     ▼
                         [ Transaction Analysis ]
                                     │
         ┌───────────────────────────┼───────────────────────────┐
         ▼                           ▼                           ▼
[ Risk Detection Engine ]   [ Money Flow Tracing ]     [ VASP Intelligence ]
- Fund Splitting (+20)      - Node / Edge Extraction   - VASP Registry Lookup
- Rapid Movement (+20)      - Multi-hop Mapping        - Off-chain Boundary Tag
- Multi-hop Layering (+15)  - Flow Consolidation       - LAST TRACEABLE POINT
- Flagged Addresses (+20)            │                           │
- VASP Interaction (+15)             │                           │
         │                           │                           │
         ▼                           ▼                           ▼
  [ Heuristic Risk ]          [ Graph Model ]             [ VASP Handover ]
         └───────────────────────────┼───────────────────────────┘
                                     ▼
                    [ AI-Assisted Narrative Briefing ]
                      (API with Rule-Based Fallback)
                                     │
                                     ▼
                      [ Investigation Dossier / Report ]
                                     │
                                     ▼
                     [ React.js Investigation Dashboard ]
```

---

## 📁 Repository Directory Structure

```text
Crypto-Fraud-VASP-Intelligence/
├── README.md                      # Master Project Overview (This file)
├── .gitignore                     # Git ignore rules for Java, Node, IDEs
├── .env.example                   # Environment variables template
│
├── docs/                          # Comprehensive Team Documentation
│   ├── architecture.md            # High-level architecture & data flow
│   ├── api-contract.md            # REST API specifications & JSON models
│   └── workflow.md                # 5-day hackathon plan & Git rules
│
├── backend/
│   └── springboot-project/        # Java 17 + Spring Boot 3 Backend
│       ├── pom.xml                # Maven build definition
│       ├── README.md              # Backend developer instructions
│       └── src/main/...           # Java application & starter models
│
├── frontend/
│   └── react-project/             # React.js + Vite Frontend
│       ├── package.json           # Frontend dependencies (React, Cytoscape)
│       ├── README.md              # Frontend developer instructions
│       └── src/...                # Dashboard UI components & styling
│
├── data/                          # Canonical Demo / Test Datasets
│   ├── sample-transactions.json   # Multi-hop scam demo dataset
│   └── sample-vasps.json          # Fictional VASP registry dataset
│
└── reports/                       # Generated Investigation Dossiers
    ├── sample-investigation-report.json
    └── README.md
```

---

## 🚀 Quickstart Guide

### Prerequisites
- **Java Development Kit (JDK 17+)**
- **Node.js (v18+) & npm**
- **Git**

### 1. Backend Setup (Spring Boot)
```bash
# Navigate to backend directory
cd backend/springboot-project

# Run with Maven (or open project in IntelliJ IDEA / VSCode)
# Using Maven wrapper or installed mvn:
mvn spring-boot:run
```
*The backend REST API starts at:* `http://localhost:8080`

### 2. Frontend Setup (React + Vite)
```bash
# Navigate to frontend directory
cd frontend/react-project

# Install dependencies
npm install

# Start Vite development server
npm run dev
```
*The frontend dashboard opens at:* `http://localhost:5173`

---

## 📚 Essential Documentation
- [Architecture Details](docs/architecture.md): In-depth component guide and system flow.
- [REST API Contract](docs/api-contract.md): Strict JSON contracts and endpoint specifications.
- [Git Workflow & 5-Day Sprint Plan](docs/workflow.md): Git branch policy, Definition of Done, and daily milestones.
