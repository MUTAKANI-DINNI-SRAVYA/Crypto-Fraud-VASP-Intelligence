# Investigation Reports Directory

This folder stores generated crypto investigation dossiers and forensic case summaries.

## Directory Contents
- `sample-investigation-report.json`: Canonical reference dossier generated from `data/sample-transactions.json`.

## Generating Reports
Reports are compiled by **Member 6 (VASP + AI + Report Engineer)** via the backend endpoint:
```
POST /api/report/generate
```

Each report combines:
1. Target wallet transaction summary
2. Heuristic risk calculation & triggered rules
3. Matched VASP entities and designated **LAST TRACEABLE POINTS**
4. AI-assisted executive summary (or deterministic fallback)
5. Legal & forensic disclaimers
