import React from 'react';

export default function AiExplanation({ aiData }) {
  if (!aiData) {
    return (
      <div className="card">
        <h2 className="card-title">🤖 AI-Assisted Investigation Briefing</h2>
        <p className="text-muted">AI briefing will appear once wallet analysis is complete.</p>
      </div>
    );
  }

  const {
    suspiciousBehavior = '',
    investigationSummary = '',
    importantFindings = [],
    handoverNotes = '',
    aiSummary = '',
    isFallback = false,
    modelName = 'Gemini 1.5 Pro (via Backend / Contract Fallback)',
  } = aiData;

  const mainSummary = investigationSummary || aiSummary || 'No summary available.';

  return (
    <div className="card ai-card-wrapper">
      <div className="card-header-flex">
        <div className="ai-title-row">
          <h2 className="card-title">
            <span className="card-icon">🤖</span>
            AI-Assisted Investigation Briefing
          </h2>
          <span className="ai-model-pill">
            <span className="sparkle">✨</span>
            {modelName}
          </span>
        </div>

        {isFallback && (
          <span className="fallback-pill" title="Pre-generated synthesis adhering to API contract format">
            Standard Synthesis
          </span>
        )}
      </div>

      <div className="ai-sections-grid">
        {/* Executive Summary */}
        <div className="ai-block summary-block">
          <h3 className="ai-block-title">
            <span>📋</span> Executive Investigation Summary
          </h3>
          <p className="ai-text">{mainSummary}</p>
        </div>

        {/* Suspicious Pattern Explanation */}
        {suspiciousBehavior && (
          <div className="ai-block behavior-block">
            <h3 className="ai-block-title">
              <span>⚠️</span> Behavioral Pattern Breakdown
            </h3>
            <p className="ai-text">{suspiciousBehavior}</p>
          </div>
        )}

        {/* Key Findings List */}
        {importantFindings && importantFindings.length > 0 && (
          <div className="ai-block findings-block">
            <h3 className="ai-block-title">
              <span>🎯</span> Key On-Chain Intelligence Findings
            </h3>
            <ul className="findings-list">
              {importantFindings.map((finding, idx) => (
                <li key={idx} className="finding-item">
                  <span className="finding-bullet">✔</span>
                  <span>{finding}</span>
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Investigator Handover Notes */}
        {handoverNotes && (
          <div className="ai-block handover-block">
            <h3 className="ai-block-title">
              <span>🛡️</span> Investigator Handover &amp; Legal Next Steps
            </h3>
            <div className="handover-box">
              <p className="ai-text">{handoverNotes}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
