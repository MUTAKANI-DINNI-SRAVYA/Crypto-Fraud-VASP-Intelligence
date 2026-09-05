import React from 'react';

export default function VaspPanel({ vaspFindings = [] }) {
  if (!vaspFindings || vaspFindings.length === 0) {
    return (
      <div className="card vasp-card-wrapper">
        <div className="card-header-flex">
          <h2 className="card-title">
            <span className="card-icon">🏛️</span>
            VASP Intelligence &amp; Attribution
          </h2>
          <span className="badge-pill badge-neutral">No VASP Endpoints Detected</span>
        </div>
        <div className="empty-state-box">
          <p className="text-muted">
            No known Virtual Asset Service Provider (VASP) custodial addresses were identified in the direct
            hops of this address.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="card vasp-card-wrapper">
      <div className="card-header-flex">
        <div>
          <h2 className="card-title">
            <span className="card-icon">🏛️</span>
            Virtual Asset Service Provider (VASP) Intelligence
          </h2>
          <span className="card-subtitle">
            Custodial boundaries and Last Traceable Points on the public ledger
          </span>
        </div>
        <span className="vasp-count-pill">{vaspFindings.length} VASP Endpoint(s)</span>
      </div>

      {/* Prominent Last Traceable Point Warning Banner */}
      <div className="vasp-alert-banner">
        <div className="vasp-alert-icon">⚠️</div>
        <div className="vasp-alert-content">
          <div className="vasp-alert-header">
            <span className="vasp-tag-critical">LAST TRACEABLE POINT REACHED</span>
            <span className="vasp-alert-entity">Omnibus Hot Wallet Deposit</span>
          </div>
          <p className="vasp-alert-desc">
            Public blockchain visibility ceases once funds enter custodial exchange omnibus accounts.
            To ascertain account holder KYC identity or fiat off-ramp details, legal process (e.g., subpoena, court order, or mutual legal assistance) must be served to the identified VASP entities.
          </p>
        </div>
      </div>

      <div className="vasp-grid">
        {vaspFindings.map((vasp, idx) => (
          <div key={vasp.address || idx} className="vasp-entity-card">
            <div className="vasp-card-top">
              <div className="vasp-badge-col">
                <span className="vasp-type-tag">{vasp.category || 'Centralized Exchange (CEX)'}</span>
                {vasp.isLastTraceablePoint && (
                  <span className="ltp-indicator">TERMINAL HOP</span>
                )}
              </div>
              <span className="vasp-country">🌍 {vasp.country || vasp.fictionalJurisdiction || 'Demo Regulatory Zone'}</span>
            </div>

            <h3 className="vasp-name">{vasp.vaspName}</h3>

            <div className="vasp-info-list">
              <div className="vasp-info-row">
                <span className="info-label">Deposit Address:</span>
                <span className="mono info-value">{vasp.address}</span>
              </div>

              <div className="vasp-info-row">
                <span className="info-label">Custodial Type:</span>
                <span className="info-value">{vasp.custodialType || 'Omnibus Deposit Hot Wallet'}</span>
              </div>

              <div className="vasp-info-row">
                <span className="info-label">Interaction Status:</span>
                <span className="status-highlight">
                  {vasp.interactionStatus || 'LAST_TRACEABLE_POINT'}
                </span>
              </div>

              {vasp.amountReceived && (
                <div className="vasp-info-row">
                  <span className="info-label">Inbound Volume:</span>
                  <span className="amount-highlight">
                    {vasp.amountReceived} {vasp.asset || 'ETH'} (Hop #{vasp.hopDistance || 3})
                  </span>
                </div>
              )}
            </div>

            {vasp.boundaryNotice && (
              <div className="vasp-legal-box">
                <strong>Attribution Notice:</strong>
                <p>{vasp.boundaryNotice}</p>
                {vasp.complianceNotice && (
                  <p className="compliance-subtext">{vasp.complianceNotice}</p>
                )}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
