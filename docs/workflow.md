# Team Workflow, Git Strategy & 5-Day Sprint Plan

This guide outlines our team collaboration rules, Git branch protocols, individual work boundaries, the 10-point Definition of Done, and our daily schedule leading up to the **September 7, 2026 Hackathon**.

---

## 1. Beginner-Friendly Git Collaboration Strategy

> [!WARNING]
> **RULE #1 FOR ALL 6 MEMBERS:**
> **NEVER PUSH DIRECTLY TO THE `main` BRANCH.**
> The `main` branch is protected and reserved exclusively for stable, integrated code tested by the Team Lead.

### 1.1 Branch Allocation Matrix
Each member works in their designated feature branch:

| Member | Role | Feature Branch | Primary Working Folder |
| :--- | :--- | :--- | :--- |
| **Member 1** | Team Lead / Integrator | `feature/integration` | `backend/.../controller/`, Root configs |
| **Member 2** | Blockchain / Data | `feature/blockchain` | `backend/.../service/blockchain/` |
| **Member 3** | Fraud & Risk Engine | `feature/risk-engine` | `backend/.../service/risk/` |
| **Member 4** | Money Flow Graph | `feature/graph` | `frontend/.../components/graph/` & `backend/.../service/graph/` |
| **Member 5** | Frontend / Dashboard | `feature/frontend` | `frontend/react-project/src/` |
| **Member 6** | VASP + AI + Report | `feature/ai-vasp` | `backend/.../service/vasp/` & `service/report/` |

---

### 1.2 Step-by-Step Daily Git Routine

Whenever you sit down to work, follow these exact 7 steps in your terminal:

```
[ Step 1: Pull Latest Main ]
           │
           ▼
[ Step 2: Switch to Your Feature Branch ]
           │
           ▼
[ Step 3: Write & Test Code Locally ]
           │
           ▼
[ Step 4: Stage Changes (`git add`) ]
           │
           ▼
[ Step 5: Commit with Clean Message (`git commit`) ]
           │
           ▼
[ Step 6: Push to GitHub (`git push`) ]
           │
           ▼
[ Step 7: Open Pull Request (PR) for Team Lead Review ]
```

#### Terminal Commands:

```bash
# 1. Update your local repo with any newly merged work
git checkout main
git pull origin main

# 2. Switch to your feature branch (Example: Member 3)
git checkout feature/risk-engine
# (If creating the branch for the first time, use: git checkout -b feature/risk-engine)

# 3. Merge latest main into your branch so you are always up to date
git merge main

# 4. Write code, test it locally to verify it works!

# 5. Stage your changes
git status
git add .

# 6. Commit with a clear, descriptive message
git commit -m "feat(risk): add rapid movement detection rule (+20 pts)"

# 7. Push your branch to GitHub
git push origin feature/risk-engine
```

#### 8. Creating the Pull Request (PR)
1. Go to your repository on GitHub.
2. Click the green button: **Compare & pull request**.
3. Set base: `main` ← compare: `your-feature-branch`.
4. Assign **Member 1 (Team Lead)** as the Reviewer.
5. In the description, paste your test results and a screenshot if frontend.

---

## 2. Team Member Work Boundaries (Zero Merge Conflicts)

To prevent code collisions and Git conflicts, each member strictly owns their folder domain:

```
                          ┌────────────────────────┐
                          │     PROJECT ROOT       │
                          └───────────┬────────────┘
                                      │
              ┌───────────────────────┴───────────────────────┐
              ▼                                               ▼
     [ backend/ ]                                    [ frontend/ ]
     ├─ controller/ (Member 1)                       ├─ components/
     ├─ service/blockchain/ (Member 2)               │   ├─ graph/ (Member 4)
     ├─ service/risk/ (Member 3)                     │   ├─ dashboard/ (Member 5)
     ├─ service/graph/ (Member 4)                    │   ├─ search/ (Member 5)
     └─ service/vasp/ & report/ (Member 6)           │   └─ vasp/ (Member 5 & 6)
                                                     └─ App.jsx & styles (Member 5)
```

- **Member 2 (Blockchain):** Works ONLY in `service/blockchain/`. Does NOT touch risk logic, frontend, or AI.
- **Member 3 (Risk):** Works ONLY in `service/risk/`. Does NOT touch blockchain network calls or frontend.
- **Member 4 (Graph):** Works ONLY on graph data structuring and Cytoscape visualization. Does NOT touch risk formulas.
- **Member 5 (Frontend):** Works ONLY in `frontend/react-project/src/`. Does NOT edit Java backend files.
- **Member 6 (VASP & AI):** Works ONLY in `service/vasp/` and `service/report/`. Does NOT edit blockchain loaders.
- **Member 1 (Integrator):** Connects the backend controllers to the frontend, manages root dependencies, and reviews PRs.

---

## 3. Definition of Done (DoD) Checklist

A feature is **NOT finished** simply because code was written or generated. A task is marked **DONE** only when all 10 criteria are met:

- [ ] **1. Code Exists:** Code is written cleanly in the designated package/folder.
- [ ] **2. Code Runs:** The code compiles and executes without runtime errors or crashes.
- [ ] **3. Basic Test Works:** A simple test or manual execution proves the logic functions as intended.
- [ ] **4. Contract Compliance:** Inputs and outputs strictly adhere to `docs/api-contract.md`.
- [ ] **5. Documentation Exists:** The class/component includes brief comments explaining what it does.
- [ ] **6. Sample Data Used:** Tested against `data/sample-transactions.json` or `data/sample-vasps.json`.
- [ ] **7. Clean Git Commit:** Committed locally with a descriptive message (`feat(...)`, `fix(...)`).
- [ ] **8. Pushed to Feature Branch:** Pushed to `feature/<name>` on GitHub.
- [ ] **9. Pull Request Created:** PR opened against `main` with summary notes.
- [ ] **10. Team Lead Approved:** Team Lead successfully ran and verified the code before merging.

---

## 4. Five-Day Emergency Hackathon Plan

```
  Sept 2 (Day 1)      Sept 3 (Day 2)      Sept 4 (Day 3)      Sept 5 (Day 4)      Sept 6 (Day 5)      Sept 7 (Day 6)
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  Architecture,  │ │  First Local    │ │   End-to-End    │ │  UI Polish,     │ │  CODE FREEZE!   │ │  HACKATHON      │
│  Contracts &    │ │  Integration    │ │   Working MVP   │ │  AI Reports &   │ │  PPT Slides,    │ │  PRESENTATION   │
│  Independent    │ │  Checkpoint     │ │   Pipeline      │ │  Error Handling │ │  Demo Rehearsal │ │  DAY!           │
│  Skeletons      │ │                 │ │                 │ │                 │ │                 │ │                 │
└─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘
```

### Day 1: Wednesday, Sept 2 (Today) - Foundations & Independent Kick-Off
- **Target:** Everyone understands the architecture, has repo cloned, and starts independent module logic.
- **Member 1:** Commit architecture, data contracts, API specs, and project skeleton to GitHub.
- **Member 2:** Build mock transaction file reader and research Etherscan API endpoints.
- **Member 3:** Write Java unit tests for fund-splitting and rapid-movement risk rules using mock data.
- **Member 4:** Install Cytoscape.js and render a basic 3-node graph dummy component.
- **Member 5:** Set up React dashboard layout with search input and placeholder cards.
- **Member 6:** Build VASP lookup service matching addresses against `sample-vasps.json`.

### Day 2: Thursday, Sept 3 - First Integration Checkpoint
- **Target:** Backend services communicate with mock data; Frontend connects to Spring Boot.
- **Backend:** Connect Member 2's loader, Member 3's risk engine, and Member 6's VASP check into Member 1's `RestController`.
- **Frontend:** Test `axios`/`fetch` calls from React (`http://localhost:5173`) to Spring Boot (`http://localhost:8080/api`).
- **Milestone:** Clicking "Search" on the dashboard returns mock transactions and risk scores over HTTP.

### Day 3: Friday, Sept 4 - Complete End-to-End MVP
- **Target:** Full pipeline working from wallet query to graph rendering and risk evaluation.
- **Pipeline:** Querying `0x71C8...` loads the multi-hop transaction dataset, runs through the risk engine (score: 75 / High), highlights the VASP node, and tags the "LAST TRACEABLE POINT".
- **Member 4 & 5:** Graph renders nodes with color coding (Green = Victim, Red = Scam, Orange = Hops, Blue = VASP).

### Day 4: Saturday, Sept 5 - UI Polish, AI Narrative & Report Export
- **Target:** Professional investigator experience, edge cases, and report generation.
- **Member 6:** Integrate free AI prompt (or fallback narrative) and build the investigation report generator (`/api/report/generate`).
- **Member 5:** Add sleek dark theme, risk gauge animations, and transaction filter search.
- **All:** Test error scenarios (invalid addresses, empty transactions, backend offline).

### Day 5: Sunday, Sept 6 - CODE FREEZE & Presentation Preparation
- **CRITICAL RULE:** **NO NEW MAJOR FEATURES ALLOWED TODAY.**
- **Morning:** Smoke testing, verify everything works with zero errors.
- **Afternoon:** Prepare presentation slide deck (Problem statement, Architecture, Live Demo flow, Legal/On-chain Limitations, Future Roadmap).
- **Evening:** 3 dry-run presentation rehearsals. Prepare backup screen recording in case of live demo wifi issues!

### Day 6: Monday, Sept 7 - Hackathon Day! 🏆
- Deliver demo with high confidence, clear division of presentation speaking roles, and working prototype!
