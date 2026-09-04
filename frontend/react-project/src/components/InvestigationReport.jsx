import React, { useState } from 'react';

export default function InvestigationReport({
  reportData,
  targetAddress,
  onGenerateReport,
  isGenerating,
}) {
  const [modalOpen, setModalOpen] = useState(false);

  const handleOpenModal = () => {
    if (!reportData && onGenerateReport) {
      onGenerateReport();
    }
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
  };

  const handleDownloadJSON = () => {
    if (!reportData) return;
    const jsonStr = JSON.stringify(reportData, null, 2);
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `investigation-report-${reportData.reportId || targetAddress || 'case'}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="card report-card-wrapper">
      <div className="card-header-flex">
        <div>
          <h2 className="card-title">
            <span className="card-icon">📑</span>
            Investigation Dossier &amp; Formal Report
          </h2>
          <span className="card-subtitle">
            Export standardized compliance and law enforcement handover package
          </span>
        </div>

        <button
          type="button"
          className="btn-report-primary"
          onClick={handleOpenModal}
          disabled={isGenerating}
        >
          {isGenerating ? (
            <span>Compiling Report...</span>
          ) : (
            <span>📄 Generate Investigation Report</span>
          )}
        </button>
      </div>

      <div className="report-summary-bar">
        <div className="report-meta-chip">
          <span className="meta-label">Case Status:</span>
          <span className="meta-val badge-active">PRELIMINARY_TRIAGE</span>
        </div>
        <div className="report-meta-chip">
          <span className="meta-label">Format:</span>
          <span className="meta-val">Structured JSON / Dossier View</span>
        </div>
        <div className="report-meta-chip">
          <span className="meta-label">Jurisdiction Notice:</span>
          <span className="meta-val">LTP Boundaries Documented</span>
        </div>
      </div>

      {/* Modal View for the Dossier */}
      {modalOpen && (
        <div className="modal-backdrop" onClick={handleCloseModal}>
          <div className="modal-container" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <div className="modal-title-col">
                <span className="modal-kicker">CRYPTO FRAUD &amp; VASP INTELLIGENCE REPORT</span>
                <h3 className="modal-title">
                  Case File: {reportData?.reportId || `IR-${targetAddress?.substring(0, 10)}`}
                </h3>
              </div>
              <div className="modal-actions-top">
                <button
                  type="button"
                  className="btn-download"
                  onClick={handleDownloadJSON}
                  title="Download structured JSON report"
                >
                  💾 Download JSON
                </button>
                <button
                  type="button"
                  className="btn-print"
                  onClick={handlePrint}
                  title="Print dossier"
                >
                  🖨️ Print
                </button>
                <button
                  type="button"
                  className="btn-modal-close"
                  onClick={handleCloseModal}
                  title="Close modal"
                >
                  ✕
                </button>
              </div>
            </div>

            <div className="modal-body printable-dossier">
              {/* Report Header Metadata */}
              <div className="dossier-meta-grid">
                <div className="dossier-item">
                  <span className="d-label">Target Wallet Address:</span>
                  <span className="d-val mono">{reportData?.targetAddress || targetAddress}</span>
                </div>
                <div className="dossier-item">
                  <span className="d-label">Generated Timestamp (UTC):</span>
                  <span className="d-val">
                    {reportData?.generatedAt || new Date().toUTCString()}
                  </span>
                </div>
                <div className="dossier-item">
                  <span className="d-label">Investigator Assigned:</span>
                  <span className="d-val">{reportData?.investigator || 'Security Intelligence Analyst'}</span>
                </div>
                <div className="dossier-item">
                  <span className="d-label">Case Classification:</span>
                  <span className="d-val text-rose font-bold">
                    {reportData?.caseStatus || 'HIGH_RISK_SUSPECTED_FRAUD'}
                  </span>
                </div>
              </div>

              <hr className="dossier-divider" />

              {/* Heuristic Risk Section */}
              <div className="dossier-section">
                <h4 className="dossier-heading">1. Heuristic Risk Evaluation</h4>
                <div className="dossier-risk-summary">
                  <div>
                    <strong>Risk Score: </strong>
                    <span className="text-rose font-bold">
                      {reportData?.riskEvaluation?.heuristicScore ??
                        reportData?.riskEvaluation?.riskScore ??
                        85}
                      /100
                    </span>
                  </div>
                  <div>
                    <strong>Risk Classification: </strong>
                    <span className="badge-critical">
                      {reportData?.riskEvaluation?.riskLevel || 'HIGH'}
                    </span>
                  </div>
                </div>

                <div className="dossier-rules-list">
                  {(reportData?.riskEvaluation?.triggeredRules || []).map((r, i) => (
                    <div key={i} className="dossier-rule-item">
                      <span className="rule-badge">+{r.scoreDelta || 20}</span>
                      <div>
                        <strong>{r.ruleName || r.ruleId}</strong>
                        <p className="text-sm">{r.detail || r.description}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <hr className="dossier-divider" />

              {/* AI Executive Summary */}
              <div className="dossier-section">
                <h4 className="dossier-heading">2. AI Executive Briefing</h4>
                <p className="dossier-ai-summary">
                  {reportData?.aiExecutiveSummary ||
                    'Investigation reveals rapid fund dispersion across multiple downstream accounts, terminating at identified VASP custodial deposit addresses.'}
                </p>
              </div>

              <hr className="dossier-divider" />

              {/* VASP Attributions & Last Traceable Points */}
              <div className="dossier-section">
                <h4 className="dossier-heading">3. VASP Identified &amp; Last Traceable Points</h4>
                <div className="dossier-vasp-list">
                  {(reportData?.vaspFindings || []).map((v, idx) => (
                    <div key={idx} className="dossier-vasp-box">
                      <div className="dossier-vasp-title-row">
                        <strong>{v.vaspName}</strong>
                        <span className="badge-ltp">LAST TRACEABLE POINT</span>
                      </div>
                      <div className="text-sm">
                        <span><strong>Deposit Address: </strong> <span className="mono">{v.address || v.walletAddress}</span></span>
                      </div>
                      <div className="text-sm">
                        <span><strong>Custodial Inflow: </strong> {v.amountReceived} {v.asset || 'ETH'} (Hop #{v.hopDistance || 3})</span>
                      </div>
                      <div className="dossier-notice-box">
                        <small>⚖️ {v.legalNotice || v.boundaryNotice}</small>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <hr className="dossier-divider" />

              {/* Disclaimers */}
              <div className="dossier-disclaimers">
                <h4 className="dossier-heading">4. Ethical &amp; Technical Limitations</h4>
                <p>
                  <strong>Heuristic Notice:</strong>{' '}
                  {reportData?.disclaimers?.heuristicDisclaimer ||
                    'This score and analysis is an automated prototype heuristic indicator intended for investigative triage. It is not an AML compliance determination or proof of illegal conduct.'}
                </p>
                <p>
                  <strong>On-Chain Limitation:</strong>{' '}
                  {reportData?.disclaimers?.onChainLimitation ||
                    'Public blockchain data does not contain KYC identity. Lawful subpoenas required to bridge the on-chain to off-chain gap.'}
                </p>
              </div>
            </div>

            <div className="modal-footer">
              <button type="button" className="btn-secondary" onClick={handleCloseModal}>
                Close Dossier
              </button>
              <button type="button" className="btn-primary" onClick={handleDownloadJSON}>
                Download JSON Report
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
