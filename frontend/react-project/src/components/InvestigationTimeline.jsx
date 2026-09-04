import React from 'react';

export default function InvestigationTimeline({ transactions = [], targetAddress = '' }) {
  if (!transactions || transactions.length === 0) {
    return (
      <div className="card">
        <h2 className="card-title">⏱️ Chronological Flow Timeline</h2>
        <p className="text-muted">No timeline events recorded.</p>
      </div>
    );
  }

  // Sort chronologically ascending
  const sortedTxs = [...transactions].sort((a, b) => {
    const timeA = new Date(a.timestamp).getTime() || 0;
    const timeB = new Date(b.timestamp).getTime() || 0;
    return timeA - timeB;
  });

  const formatShortTime = (ts) => {
    if (!ts) return 'N/A';
    try {
      const d = new Date(ts);
      return isNaN(d.getTime()) ? ts : d.toISOString().replace('T', ' ').replace('.000Z', ' UTC');
    } catch {
      return ts;
    }
  };

  const getStepType = (tx, index) => {
    if (tx.direction === 'IN' || index === 0) {
      return {
        type: 'INFLOW',
        badgeClass: 'timeline-badge-in',
        title: 'Initial Fund Inflow / Ingestion',
        icon: '📥',
      };
    }
    if (tx.direction === 'TERMINUS' || tx.to?.toUpperCase().includes('VASP')) {
      return {
        type: 'VASP_TERMINUS',
        badgeClass: 'timeline-badge-vasp',
        title: 'Custodial VASP Deposit (Last Traceable Point)',
        icon: '🏛️',
      };
    }
    if (tx.direction === 'OUT') {
      return {
        type: 'DISPERSION',
        badgeClass: 'timeline-badge-split',
        title: 'Rapid Fund Splitting & Layering',
        icon: '⚡',
      };
    }
    return {
      type: 'HOP',
      badgeClass: 'timeline-badge-hop',
      title: 'Intermediary Relayering Hop',
      icon: '🔗',
    };
  };

  return (
    <div className="card timeline-card">
      <div className="card-header-flex">
        <h2 className="card-title">
          <span className="card-icon">⏱️</span>
          Chronological Investigation Timeline
        </h2>
        <span className="timeline-counter">{sortedTxs.length} Sequential Events</span>
      </div>

      <div className="timeline-container">
        {sortedTxs.map((tx, idx) => {
          const step = getStepType(tx, idx);
          return (
            <div key={tx.hash || idx} className="timeline-item">
              <div className="timeline-marker-col">
                <div className={`timeline-marker ${step.badgeClass}`}>
                  <span>{step.icon}</span>
                </div>
                {idx < sortedTxs.length - 1 && <div className="timeline-line"></div>}
              </div>

              <div className="timeline-content">
                <div className="timeline-header">
                  <span className={`timeline-type-pill ${step.badgeClass}`}>
                    {step.title}
                  </span>
                  <span className="timeline-timestamp">
                    {formatShortTime(tx.timestamp)}
                  </span>
                </div>

                <div className="timeline-body">
                  <div className="timeline-flow-route">
                    <span className="mono addr-crumb">
                      {tx.from ? `${tx.from.substring(0, 8)}...` : 'Unknown'}
                    </span>
                    <span className="flow-arrow">➔</span>
                    <span className="mono addr-crumb">
                      {tx.to ? `${tx.to.substring(0, 8)}...` : 'Unknown'}
                    </span>
                  </div>

                  <div className="timeline-details-row">
                    <span className="timeline-amount">
                      <strong>{Number(tx.amount || 0).toFixed(2)}</strong> {tx.asset || 'ETH'}
                    </span>
                    <span className="timeline-hash mono" title={tx.hash}>
                      Tx: {tx.hash ? `${tx.hash.substring(0, 10)}...` : ''}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
