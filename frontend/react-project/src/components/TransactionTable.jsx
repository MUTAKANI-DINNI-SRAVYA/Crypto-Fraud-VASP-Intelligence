import React, { useState } from 'react';

export default function TransactionTable({ transactions = [], targetAddress = '' }) {
  const [copiedText, setCopiedText] = useState(null);

  const copyToClipboard = (text, label) => {
    if (navigator?.clipboard?.writeText) {
      navigator.clipboard.writeText(text);
      setCopiedText(label);
      setTimeout(() => setCopiedText(null), 1800);
    }
  };

  const formatAddress = (addr) => {
    if (!addr) return 'Unknown';
    if (addr.length <= 16) return addr;
    return `${addr.substring(0, 8)}...${addr.substring(addr.length - 6)}`;
  };

  const formatHash = (h) => {
    if (!h) return 'Unknown';
    if (h.length <= 16) return h;
    return `${h.substring(0, 10)}...${h.substring(h.length - 6)}`;
  };

  const formatTime = (ts) => {
    if (!ts) return 'N/A';
    try {
      const date = new Date(ts);
      return isNaN(date.getTime()) ? ts : date.toUTCString().replace('GMT', 'UTC');
    } catch {
      return ts;
    }
  };

  const getDirectionTag = (tx) => {
    if (tx.direction) {
      if (tx.direction === 'IN') return <span className="tag-in">INCOMING</span>;
      if (tx.direction === 'OUT') return <span className="tag-out">DISPERSION</span>;
      if (tx.direction === 'TERMINUS') return <span className="tag-vasp">VASP DEPOSIT</span>;
      return <span className="tag-hop">HOP</span>;
    }
    const cleanTarget = targetAddress?.toLowerCase();
    if (cleanTarget && tx.to?.toLowerCase() === cleanTarget) {
      return <span className="tag-in">INCOMING</span>;
    }
    if (cleanTarget && tx.from?.toLowerCase() === cleanTarget) {
      return <span className="tag-out">DISPERSION</span>;
    }
    return <span className="tag-hop">HOP</span>;
  };

  if (!transactions || transactions.length === 0) {
    return (
      <div className="card">
        <h2 className="card-title">📜 On-Chain Transaction Ledger</h2>
        <div className="empty-state-box">
          <span className="empty-icon">📭</span>
          <p>No transaction history discovered for this address.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="card tx-card-container">
      <div className="card-header-flex">
        <h2 className="card-title">
          <span className="card-icon">📜</span>
          On-Chain Transaction Ledger
        </h2>
        <div className="tx-count-pill">
          {transactions.length} Records Found
          {copiedText && <span className="copy-notif">Copied {copiedText}!</span>}
        </div>
      </div>

      <div className="table-responsive">
        <table className="tx-table">
          <thead>
            <tr>
              <th>Flow</th>
              <th>Tx Hash</th>
              <th>From Address</th>
              <th>To Address</th>
              <th>Amount</th>
              <th>Asset</th>
              <th>Timestamp (UTC)</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((tx, idx) => (
              <tr key={tx.hash || idx} className="tx-row">
                <td>{getDirectionTag(tx)}</td>
                <td>
                  <button
                    type="button"
                    className="mono copyable-btn"
                    onClick={() => copyToClipboard(tx.hash, 'Tx Hash')}
                    title={`Click to copy: ${tx.hash}`}
                  >
                    {formatHash(tx.hash)}
                    <span className="copy-icon">📋</span>
                  </button>
                </td>
                <td>
                  <button
                    type="button"
                    className={`mono copyable-btn ${
                      targetAddress && tx.from?.toLowerCase() === targetAddress.toLowerCase()
                        ? 'highlight-target'
                        : ''
                    }`}
                    onClick={() => copyToClipboard(tx.from, 'From Address')}
                    title={`Click to copy: ${tx.from}`}
                  >
                    {formatAddress(tx.from)}
                  </button>
                </td>
                <td>
                  <button
                    type="button"
                    className={`mono copyable-btn ${
                      targetAddress && tx.to?.toLowerCase() === targetAddress.toLowerCase()
                        ? 'highlight-target'
                        : ''
                    }`}
                    onClick={() => copyToClipboard(tx.to, 'To Address')}
                    title={`Click to copy: ${tx.to}`}
                  >
                    {formatAddress(tx.to)}
                  </button>
                </td>
                <td className="amount-col">
                  <strong>{Number(tx.amount || 0).toFixed(2)}</strong>
                </td>
                <td>
                  <span className="asset-tag">{tx.asset || 'ETH'}</span>
                </td>
                <td className="time-col">{formatTime(tx.timestamp)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
