# React Frontend: Crypto Fraud & VASP Intelligence

This is the investigator dashboard built with **React.js 18** and **Vite**.

---

## 🛠️ Tech Stack & Dependencies
- **Runtime:** Node.js (v18+) & npm
- **Framework:** React 18
- **Build Tool:** Vite
- **Graph Visualization:** Cytoscape.js
- **Icons:** Lucide React
- **Dev Server Port:** `5173` (Proxied to backend at `http://localhost:8080`)

---

## 📂 Folder Structure & Responsibilities

```
src/
├── App.jsx             # Main dashboard layout (Member 5: Frontend Engineer)
├── index.css           # Modern cyber-investigator theme & styles
├── main.jsx            # React root mount
│
├── components/         # Modular Components
│   ├── graph/          # Member 4: Cytoscape.js money flow canvas
│   ├── risk/           # Member 5: Heuristic risk gauge & triggered rules
│   ├── ledger/         # Member 5: On-chain transaction table
│   └── report/         # Member 5 & 6: VASP intelligence & AI briefing modal
```

---

## 🚀 How to Run the Frontend Locally

```bash
# 1. Navigate to frontend directory
cd frontend/react-project

# 2. Install dependencies (React, Vite, Cytoscape)
npm install

# 3. Start local development server
npm run dev
```

Open your browser and navigate to:
```
http://localhost:5173
```

---

## 🔗 Connecting with the Backend
- Vite is configured with a built-in proxy in `vite.config.js`.
- Any frontend request to `/api/*` is automatically forwarded to Spring Boot at `http://localhost:8080/api/*`.
- If the backend is not yet running, the dashboard will display:
  `Backend offline (Local Mock Mode Active)`.

---

## 💡 Quick Tips for Teammates
1. **Member 4 (Graph Engineer):** Create a component `src/components/graph/MoneyFlowCanvas.jsx` using `cytoscape`. Use the `nodes` and `edges` format defined in `docs/api-contract.md`.
2. **Member 5 (Frontend Engineer):** Keep UI state in `App.jsx` or a simple React state hook. Do NOT introduce complex state management like Redux.
3. **Responsive & Clear:** Make sure wallet addresses use monospace font (`font-family: var(--font-mono)`) and have copy buttons for ease of use.
