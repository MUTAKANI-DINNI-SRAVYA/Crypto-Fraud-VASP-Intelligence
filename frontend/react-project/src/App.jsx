import React, { useState, useEffect } from 'react';
import {
  checkBackendHealth,
  investigateWallet,
  generateInvestigationReport,
} from './services/api';
import { DEMO_ADDRESSES } from './services/mockData';

// Reusable UI Components
import WalletSearch from './components/WalletSearch';
import RiskSummary from './components/RiskSummary';
import FundSummary from './components/FundSummary';
import TransactionTable from './components/TransactionTable';
import MoneyFlowGraph from './components/MoneyFlowGraph';
import InvestigationTimeline from './components/InvestigationTimeline';
import VaspPanel from './components/VaspPanel';
import AiExplanation from './components/AiExplanation';
import InvestigationReport from './components/InvestigationReport';

export default function App() {
  const [currentAddress, setCurrentAddress] = useState(DEMO_ADDRESSES.SCAM);
  const [investigationData, setInvestigationData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isGeneratingReport, setIsGeneratingReport] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const [backendStatus, setBackendStatus] = useState('Checking backend status...');
  const [isBackendUp, setIsBackendUp] = useState(false);

  // Initial load: verify Spring Boot status and load initial demo intelligence
  useEffect(() => {
    let isMounted = true;

    async function init() {
      const health = await checkBackendHealth();
      if (!isMounted) return;

      setIsBackendUp(health.isUp);
      setBackendStatus(health.message);

      // Trigger default investigation for demo scam address
      handleInvestigate(DEMO_ADDRESSES.SCAM);
    }

    init();

    return () => {
      isMounted = false;
    };
  }, []);

  // Handler to perform full investigation workflow
  const handleInvestigate = async (address) => {
    if (!address) return;

    setIsLoading(true);
    setErrorMessage(null);
    setCurrentAddress(address);

    try {
      const result = await investigateWallet(address);
      setInvestigationData(result);

      if (result.isMockFallback) {
        setBackendStatus('Backend offline (Local Mock Fallback Active)');
        setIsBackendUp(false);
      } else {
        setBackendStatus('Connected to Spring Boot API');
        setIsBackendUp(true);
      }
    } catch (err) {
      console.error('Investigation error:', err);
      setErrorMessage(
        err.message || 'Failed to complete investigation. Please verify the address and try again.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  // Handler for report generation
  const handleGenerateReport = async () => {
    if (!currentAddress) return;
    setIsGeneratingReport(true);
    try {
      const report = await generateInvestigationReport(currentAddress);
      setInvestigationData((prev) => ({
        ...prev,
        report,
      }));
    } catch (err) {
      console.warn('Backend report generation endpoint failed, using local dossier:', err);
    } finally {
      setIsGeneratingReport(false);
    }
  };

  return (
    <div className="app-container">
      {/* Header */}
      <header className="header">
        <div className="brand-title">
          <span className="brand-logo">🛡️⛓️</span>
          <div className="brand-text-group">
            <h1 className="brand-heading">Crypto Fraud &amp; VASP Intelligence</h1>
            <span className="badge-hackathon">Investigator Dashboard • Prototype 2026</span>
          </div>
        </div>

        <div className="header-right">
          <div className={`status-pill ${isBackendUp ? 'status-up' : 'status-fallback'}`}>
            <span
              className="status-dot"
              style={{ background: isBackendUp ? '#10b981' : '#f59e0b' }}
            ></span>
            <span>{backendStatus}</span>
          </div>
        </div>
      </header>

      {/* 1. WALLET SEARCH */}
      <WalletSearch
        currentAddress={currentAddress}
        onInvestigate={handleInvestigate}
        isLoading={isLoading}
      />

      {/* Error Message Banner */}
      {errorMessage && (
        <div className="error-alert-banner">
          <div className="error-alert-content">
            <span className="error-icon">⚠️</span>
            <div>
              <strong>Investigation Notice:</strong> {errorMessage}
            </div>
          </div>
          <button
            type="button"
            className="btn-dismiss"
            onClick={() => setErrorMessage(null)}
          >
            ✕
          </button>
        </div>
      )}

      {/* Loading Overlay / Indicator */}
      {isLoading && (
        <div className="loading-state-container">
          <div className="spinner-large"></div>
          <p className="loading-title">Extracting On-Chain &amp; VASP Intelligence...</p>
          <span className="loading-subtext">
            Tracing hops, calculating heuristic risk scores, and querying VASP registries
          </span>
        </div>
      )}

      {/* Main Investigator Intelligence Flow */}
      {!isLoading && investigationData && (
        <main className="investigation-flow">
          {/* Fallback Notice Bar */}
          {investigationData.isMockFallback && (
            <div className="mock-banner">
              <span className="mock-badge">LOCAL MOCK MODE</span>
              <span>
                Backend API is currently offline. Intelligence data rendered from local prototype contract fallback.
              </span>
            </div>
          )}

          {/* 2. RISK SUMMARY */}
          <section className="flow-section">
            <RiskSummary riskData={investigationData.risk} />
          </section>

          {/* 3. FUND SUMMARY */}
          <section className="flow-section">
            <FundSummary
              summary={investigationData.summary}
              transactions={investigationData.transactions}
            />
          </section>

          {/* 4. TRANSACTIONS */}
          <section className="flow-section">
            <TransactionTable
              transactions={investigationData.transactions}
              targetAddress={investigationData.targetAddress}
            />
          </section>

          {/* 5. MONEY FLOW GRAPH */}
          <section className="flow-section">
            <MoneyFlowGraph
              graphData={investigationData.graphData}
              targetAddress={investigationData.targetAddress}
            />
          </section>

          {/* 6. TIMELINE */}
          <section className="flow-section">
            <InvestigationTimeline
              transactions={investigationData.transactions}
              targetAddress={investigationData.targetAddress}
            />
          </section>

          {/* 7. VASP INFORMATION */}
          <section className="flow-section">
            <VaspPanel vaspFindings={investigationData.vaspFindings} />
          </section>

          {/* 8. AI EXPLANATION */}
          <section className="flow-section">
            <AiExplanation aiData={investigationData.aiExplanation} />
          </section>

          {/* 9. INVESTIGATION REPORT */}
          <section className="flow-section">
            <InvestigationReport
              reportData={investigationData.report}
              targetAddress={investigationData.targetAddress}
              onGenerateReport={handleGenerateReport}
              isGenerating={isGeneratingReport}
            />
          </section>
        </main>
      )}

      {/* Ethical & Prototype Legal Disclaimer Footer */}
      <footer className="footer">
        <div className="footer-content">
          <p>
            <strong>Prototype Disclaimer:</strong> This application is a hackathon research prototype and automated triage assistant.
          </p>
          <p>
            Risk scores are heuristic indicators and do not establish criminal culpability or AML compliance determinations.
            Public blockchain records do not hold customer identity. VASP endpoints signify Last Traceable Points requiring lawful legal process.
          </p>
          <span className="footer-copy">Crypto Fraud &amp; VASP Intelligence • Member 5 (Frontend)</span>
        </div>
      </footer>
    </div>
  );
}
