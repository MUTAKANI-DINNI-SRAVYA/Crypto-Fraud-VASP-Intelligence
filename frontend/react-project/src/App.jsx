import React, { useState, useEffect } from 'react';
import MoneyFlowGraph from './components/MoneyFlowGraph';

// Mock transaction data illustrating Victim -> Scam -> Splitting (A, B) -> Reconsolidation (D) -> VASP
const MOCK_TRANSACTIONS = [
  {
    hash: "0x1000000000000000000000000000000000000001",
    from: "0xVIC1111111111111111111111111111111111111",
    to: "0xSCAM999999999999999999999999999999999999",
    amount: 5.00,
    asset: "ETH",
    timestamp: "2026-09-02T10:00:00Z"
  },
  {
    hash: "0x2000000000000000000000000000000000000002",
    from: "0xSCAM999999999999999999999999999999999999",
    to: "0xAAAA111111111111111111111111111111111111",
    amount: 2.00,
    asset: "ETH",
    timestamp: "2026-09-02T10:05:00Z"
  },
  {
    hash: "0x2000000000000000000000000000000000000003",
    from: "0xSCAM999999999999999999999999999999999999",
    to: "0xAAAA111111111111111111111111111111111111",
    amount: 0.50,
    asset: "ETH",
    timestamp: "2026-09-02T10:07:00Z"
  },
  {
    hash: "0x3000000000000000000000000000000000000004",
    from: "0xSCAM999999999999999999999999999999999999",
    to: "0xBBBB222222222222222222222222222222222222",
    amount: 1.50,
    asset: "ETH",
    timestamp: "2026-09-02T10:06:00Z"
  },
  {
    hash: "0x4000000000000000000000000000000000000005",
    from: "0xAAAA111111111111111111111111111111111111",
    to: "0xDDDD444444444444444444444444444444444444",
    amount: 1.80,
    asset: "ETH",
    timestamp: "2026-09-02T10:15:00Z"
  },
  {
    hash: "0x5000000000000000000000000000000000000006",
    from: "0xBBBB222222222222222222222222222222222222",
    to: "0xDDDD444444444444444444444444444444444444",
    amount: 1.40,
    asset: "ETH",
    timestamp: "2026-09-02T10:18:00Z"
  },
  {
    hash: "0x6000000000000000000000000000000000000007",
    from: "0xDDDD444444444444444444444444444444444444",
    to: "0xVASP888888888888888888888888888888888888",
    amount: 3.00,
    asset: "ETH",
    timestamp: "2026-09-02T10:22:00Z"
  }
];

export default function App() {
  const [targetAddress, setTargetAddress] = useState('0xSCAM999999999999999999999999999999999999');
  const [backendStatus, setBackendStatus] = useState('Checking backend...');
  const [isBackendUp, setIsBackendUp] = useState(false);
  const [transactions, setTransactions] = useState(MOCK_TRANSACTIONS);

  // Check Spring Boot backend health on load
  useEffect(() => {
    fetch('/api/health')
      .then((res) => res.json())
      .then((data) => {
        setBackendStatus(data.message || 'Connected to Spring Boot');
        setIsBackendUp(true);
      })
      .catch(() => {
        setBackendStatus('Backend offline (Local Mock Mode Active)');
        setIsBackendUp(false);
      });
  }, []);

  const handleDemoAddress = (addr) => {
    setTargetAddress(addr);
  };

  return (
    <div className="app-container">
      {/* Header */}
      <header className="header">
        <div className="brand-title">
          <span>🛡️⛓️ Crypto Fraud &amp; VASP Intelligence</span>
          <span className="badge-hackathon">Hackathon Prototype 2026</span>
        </div>
        <div className="status-pill">
          <span className="status-dot" style={{ background: isBackendUp ? '#10b981' : '#f59e0b' }}></span>
          <span>{backendStatus}</span>
        </div>
      </header>

      {/* Search Bar Section */}
      <section className="search-section">
        <div className="search-bar-row">
          <input
            type="text"
            className="wallet-input"
            value={targetAddress}
            onChange={(e) => setTargetAddress(e.target.value)}
            placeholder="Enter Ethereum wallet address (0x...)"
          />
          <button className="btn-primary" onClick={() => alert(`Analyzing wallet: ${targetAddress}`)}>
            Analyze Wallet
          </button>
        </div>
        <div className="demo-chips">
          <span>Quick Load Demo Wallets:</span>
          <button
            className="chip-btn"
            onClick={() => handleDemoAddress('0xSCAM999999999999999999999999999999999999')}
          >
            Scam Wallet (High Risk)
          </button>
          <button
            className="chip-btn"
            onClick={() => handleDemoAddress('0xVIC1111111111111111111111111111111111111')}
          >
            Victim Wallet
          </button>
          <button
            className="chip-btn"
            onClick={() => handleDemoAddress('0xVASP888888888888888888888888888888888888')}
          >
            ApexExchange Deposit (VASP)
          </button>
        </div>
      </section>

      {/* VASP Intelligence Alert Banner */}
      <div className="vasp-banner">
        <span className="vasp-tag">LAST TRACEABLE POINT</span>
        <p className="vasp-notice">
          <strong>ApexExchange (Fictional Demo VASP):</strong> Funds entered custodial exchange omnibus pool. Public blockchain trail terminates here. <em>Further lawful off-chain records required.</em>
        </p>
      </div>

      {/* Main Grid: Risk Engine + Graph Flow */}
      <div className="dashboard-grid">
        {/* Left Column: Heuristic Risk Score */}
        <div className="card">
          <h2 className="card-title">🚨 Heuristic Risk Assessment</h2>
          <div className="risk-score-display">
            <div className="risk-number">75</div>
            <div className="risk-level-badge">HIGH RISK</div>
            <small style={{ color: 'var(--text-muted)' }}>Prototype Heuristic (Max 100)</small>
          </div>

          <div className="rules-list">
            <div className="rule-item">
              <span>Suspicious Fund Splitting</span>
              <span className="rule-score">+20</span>
            </div>
            <div className="rule-item">
              <span>Rapid Asset Relayering (&lt; 15m)</span>
              <span className="rule-score">+20</span>
            </div>
            <div className="rule-item">
              <span>Multi-Hop Layering (3 Hops)</span>
              <span className="rule-score">+15</span>
            </div>
            <div className="rule-item">
              <span>Custodial VASP Deposit</span>
              <span className="rule-score">+15</span>
            </div>
          </div>
        </div>

        {/* Right Column: Interactive Money Flow Canvas */}
        <div className="card">
          <h2 className="card-title">🕸️ Money Flow Graph Visualization (Cytoscape.js)</h2>
          <MoneyFlowGraph transactions={transactions} />
        </div>
      </div>

      {/* Lower Section: Transactions Ledger & AI Summary */}
      <div className="dashboard-grid lower-grid">
        {/* Transaction History Table */}
        <div className="card">
          <h2 className="card-title">📜 On-Chain Transaction Ledger</h2>
          <div className="tx-table-wrapper">
            <table className="tx-table">
              <thead>
                <tr>
                  <th>Tx Hash</th>
                  <th>From</th>
                  <th>To</th>
                  <th>Amount</th>
                  <th>Timestamp</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((tx, i) => (
                  <tr key={i}>
                    <td className="mono" title={tx.hash}>
                      {tx.hash ? `${tx.hash.substring(0, 6)}...${tx.hash.substring(tx.hash.length - 4)}` : `0x${i}`}
                    </td>
                    <td className="mono" title={tx.from}>
                      {tx.from ? `${tx.from.substring(0, 6)}...${tx.from.substring(tx.from.length - 4)}` : ''}
                    </td>
                    <td className="mono" title={tx.to}>
                      {tx.to ? `${tx.to.substring(0, 6)}...${tx.to.substring(tx.to.length - 4)}` : ''}
                    </td>
                    <td className="amount-cell">{tx.amount} {tx.asset || 'ETH'}</td>
                    <td className="time-cell">{tx.timestamp ? new Date(tx.timestamp).toISOString().split('T')[1].slice(0, 8) + 'Z' : '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* AI Investigation Briefing */}
        <div className="card">
          <h2 className="card-title">🤖 AI-Assisted Investigation Briefing</h2>
          <div style={{ background: 'rgba(11, 15, 25, 0.6)', padding: '1rem', borderRadius: '8px', fontSize: '0.9rem', lineHeight: '1.6', color: 'var(--text-secondary)' }}>
            <p style={{ marginBottom: '0.75rem' }}>
              <strong>Summary:</strong> The analyzed wallet exhibited rapid fund dispersion within 7 minutes of receiving 5.00 ETH from the reported victim address.
            </p>
            <p style={{ marginBottom: '0.75rem' }}>
              <strong>Handover Note:</strong> Funds branched into multiple hops, with 3.00 ETH terminating at fictional entity <em>ApexExchange</em> deposit address.
            </p>
            <p>
              <strong>Investigative Recommendation:</strong> Public ledger tracing is exhausted at the VASP boundary. Subpoena or mutual legal assistance required for off-chain customer KYC.
            </p>
          </div>
        </div>
      </div>

      {/* Ethical & Prototype Disclaimer Footer */}
      <footer className="footer">
        <p><strong>Disclaimer:</strong> This application is a hackathon research prototype and investigation assistant.</p>
        <p>It does not establish criminal guilt, make AML compliance determinations, or possess off-chain identity records. All VASP names and addresses are fictional demo entities.</p>
      </footer>
    </div>
  );
}
