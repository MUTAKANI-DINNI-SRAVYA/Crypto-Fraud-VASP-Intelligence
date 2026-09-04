import React from 'react';

export default function RiskSummary({ riskData }) {
  if (!riskData) {
    return (
      <div className="card">
        <h2 className="card-title">🚨 Heuristic Risk Assessment</h2>
        <p className="text-muted">No risk assessment data available for this address.</p>
      </div>
    );
  }

  const { riskScore = 0, riskLevel = 'LOW', triggeredRules = [], disclaimer } = riskData;

  // Color dynamics based on risk score
  const getScoreColor = (score) => {
    if (score >= 80) return 'var(--accent-rose)'; // Critical/High
    if (score >= 60) return '#fb923c'; // Orange
    if (score >= 30) return 'var(--accent-amber)'; // Medium
    return 'var(--accent-emerald)'; // Low
  };

  const getBadgeClass = (level) => {
    const l = (level || '').toUpperCase();
    if (l === 'CRITICAL' || l === 'HIGH') return 'badge-critical';
    if (l === 'MEDIUM') return 'badge-medium';
    return 'badge-low';
  };

  const scoreColor = getScoreColor(riskScore);

  return (
    <div className="card risk-card">
      <div className="card-header-flex">
        <h2 className="card-title">
          <span className="card-icon">🚨</span>
          Heuristic Risk Assessment
        </h2>
        <span className={`risk-badge ${getBadgeClass(riskLevel)}`}>{riskLevel} RISK</span>
      </div>

      <div className="risk-score-display">
        <div className="risk-meter-ring" style={{ borderColor: scoreColor }}>
          <div className="risk-number" style={{ color: scoreColor }}>
            {riskScore}
          </div>
          <span className="risk-scale-max">/ 100</span>
        </div>
        <div className="risk-status-text">
          <strong>Risk Score: {riskScore}</strong>
          <span className="risk-level-caption">Evaluated Threat Level: {riskLevel}</span>
        </div>
      </div>

      <div className="rules-section">
        <h3 className="subheading">Detected Suspicious Patterns &amp; Rules</h3>
        {triggeredRules.length === 0 ? (
          <div className="empty-rules">
            <span className="clean-icon">✅</span> No suspicious risk patterns triggered.
          </div>
        ) : (
          <div className="rules-list">
            {triggeredRules.map((rule, idx) => (
              <div key={rule.ruleId || idx} className="rule-item">
                <div className="rule-info">
                  <div className="rule-header-row">
                    <span className="rule-bullet">▸</span>
                    <strong className="rule-name">{rule.ruleName || rule.ruleId}</strong>
                  </div>
                  {rule.description && <p className="rule-desc">{rule.description}</p>}
                </div>
                {rule.scoreDelta && (
                  <div className="rule-score-tag">+{rule.scoreDelta}</div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Explicit prototype heuristic disclaimer banner */}
      <div className="heuristic-disclaimer-box">
        <span className="disclaimer-icon">ℹ️</span>
        <p className="disclaimer-text">
          {disclaimer ||
            'Notice: This score is a prototype heuristic indicator designed for investigative triage and is NOT a legal AML/KYC determination.'}
        </p>
      </div>
    </div>
  );
}
