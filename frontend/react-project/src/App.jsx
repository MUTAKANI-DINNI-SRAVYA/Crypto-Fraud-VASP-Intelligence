import React, { useState, useEffect } from 'react';

export default function App() {
  const [targetAddress, setTargetAddress] = useState('0xSCAM999999999999999999999999999999999999');
  const [backendStatus, setBackendStatus] = useState('Checking backend...');
  const [isBackendUp, setIsBackendUp] = useState(false);

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
          <div className="graph-placeholder">
            <p style={{ fontSize: '1.1rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
              [ Interactive Graph Canvas Container ]
            </p>
            <p style={{ maxWidth: '480px', textAlign: 'center', fontSize: '0.85rem' }}>
              Member 4 (Graph Engineer) will mount Cytoscape.js here, rendering Victim Wallet → Scam Wallet → Multi-Hop Nodes → Fictional VASP Endpoints.
            </p>
          </div>
        </div>
      </div>

      {/* Lower Section: Transactions Ledger & AI Summary */}
      <div className="dashboard-grid">
        {/* Transaction History Table */}
        <div className="card">
          <h2 className="card-title">📜 On-Chain Transaction Ledger</h2>
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
              <tr>
                <td className="mono">0x1000...0001</td>
                <td className="mono">0xVIC1...1111</td>
                <td className="mono">0xSCAM...9999</td>
                <td>5.00 ETH</td>
                <td>10:00:00Z</td>
              </tr>
              <tr>
                <td className="mono">0x2000...0002</td>
                <td className="mono">0xSCAM...9999</td>
                <td className="mono">0xAAAA...1111</td>
                <td>2.00 ETH</td>
                <td>10:05:00Z</td>
              </tr>
              <tr>
                <td className="mono">0x3000...0003</td>
                <td className="mono">0xSCAM...9999</td>
                <td className="mono">0xBBBB...2222</td>
                <td>1.50 ETH</td>
                <td>10:06:00Z</td>
              </tr>
              <tr>
                <td className="mono">0x6000...0006</td>
                <td className="mono">0xDDDD...4444</td>
                <td className="mono">0xVASP...8888</td>
                <td>1.95 ETH</td>
                <td>10:22:00Z</td>
              </tr>
            </tbody>
          </table>
        </div>

        {/* AI Investigation Briefing */}
        <div className="card">
          <h2 className="card-title">🤖 AI-Assisted Investigation Briefing</h2>
          <div style={{ background: 'rgba(11, 15, 25, 0.6)', padding: '1rem', borderRadius: '8px', fontSize: '0.9rem', lineHeight: '1.6', color: 'var(--text-secondary)' }}>
            <p style={{ marginBottom: '0.75rem' }}>
              <strong>Summary:</strong> The analyzed wallet exhibited rapid fund dispersion within 7 minutes of receiving 5.00 ETH from the reported victim address.
            </p>
            <p style={{ marginBottom: '0.75rem' }}>
              <strong>Handover Note:</strong> Funds branched into multiple hops, with 1.95 ETH terminating at fictional entity <em>ApexExchange</em> deposit address.
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
