import React from 'react';

export default function FundSummary({ summary, transactions = [] }) {
  // Calculate or fallback from summary object or transactions array
  const totalReceived =
    summary?.totalReceived ??
    transactions
      .filter((t) => t.direction === 'IN' || t.direction === 'TERMINUS')
      .reduce((acc, t) => acc + (Number(t.amount) || 0), 0);

  const totalSent =
    summary?.totalSent ??
    transactions
      .filter((t) => t.direction === 'OUT' || t.direction === 'HOP')
      .reduce((acc, t) => acc + (Number(t.amount) || 0), 0);

  const txCount = summary?.transactionCount ?? transactions.length;
  const hopCount = summary?.hopCount ?? (transactions.length > 2 ? 3 : 1);
  const asset = summary?.asset || 'ETH';
  const balance = summary?.balance ?? Math.max(0, +(totalReceived - totalSent).toFixed(4));

  return (
    <div className="fund-summary-container">
      <div className="summary-card inflow-card">
        <div className="summary-icon">📥</div>
        <div className="summary-details">
          <span className="summary-label">Total Incoming</span>
          <div className="summary-val-row">
            <span className="summary-value text-emerald">
              {Number(totalReceived).toFixed(2)}
            </span>
            <span className="summary-unit">{asset}</span>
          </div>
          <span className="summary-subtext">Cumulative Inflow</span>
        </div>
      </div>

      <div className="summary-card outflow-card">
        <div className="summary-icon">📤</div>
        <div className="summary-details">
          <span className="summary-label">Total Outgoing</span>
          <div className="summary-val-row">
            <span className="summary-value text-rose">
              {Number(totalSent).toFixed(2)}
            </span>
            <span className="summary-unit">{asset}</span>
          </div>
          <span className="summary-subtext">Cumulative Outflow</span>
        </div>
      </div>

      <div className="summary-card balance-card">
        <div className="summary-icon">💼</div>
        <div className="summary-details">
          <span className="summary-label">Target Balance</span>
          <div className="summary-val-row">
            <span className="summary-value text-cyan">
              {Number(balance).toFixed(2)}
            </span>
            <span className="summary-unit">{asset}</span>
          </div>
          <span className="summary-subtext">Current On-Chain Balance</span>
        </div>
      </div>

      <div className="summary-card tx-card">
        <div className="summary-icon">📊</div>
        <div className="summary-details">
          <span className="summary-label">Transactions</span>
          <div className="summary-val-row">
            <span className="summary-value text-primary">{txCount}</span>
            <span className="summary-unit">Txs</span>
          </div>
          <span className="summary-subtext">Recorded Ledger Events</span>
        </div>
      </div>

      <div className="summary-card hops-card">
        <div className="summary-icon">🕸️</div>
        <div className="summary-details">
          <span className="summary-label">Hop Depth</span>
          <div className="summary-val-row">
            <span className="summary-value text-amber">{hopCount}</span>
            <span className="summary-unit">Hops</span>
          </div>
          <span className="summary-subtext">Layering Distance</span>
        </div>
      </div>
    </div>
  );
}
