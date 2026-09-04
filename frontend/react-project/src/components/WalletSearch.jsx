import React, { useState } from 'react';
import { DEMO_ADDRESSES } from '../services/mockData';

export default function WalletSearch({ currentAddress, onInvestigate, isLoading }) {
  const [addressInput, setAddressInput] = useState(currentAddress || '');
  const [validationError, setValidationError] = useState('');

  const validateInput = (value) => {
    const trimmed = value.trim();
    if (!trimmed) {
      setValidationError('');
      return true;
    }
    const ethRegex = /^0x[a-fA-F0-9]{40}$/;
    const isDemo =
      trimmed.toUpperCase().includes('SCAM') ||
      trimmed.toUpperCase().includes('VIC') ||
      trimmed.toUpperCase().includes('VASP');

    if (!ethRegex.test(trimmed) && !isDemo) {
      setValidationError('Address must be 42 characters starting with 0x (or a valid demo tag).');
      return false;
    }
    setValidationError('');
    return true;
  };

  const handleChange = (e) => {
    const val = e.target.value;
    setAddressInput(val);
    validateInput(val);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const trimmed = addressInput.trim();
    if (!trimmed) {
      setValidationError('Please enter an Ethereum wallet address.');
      return;
    }
    if (validateInput(trimmed)) {
      onInvestigate(trimmed);
    }
  };

  const handleChipClick = (addr) => {
    setAddressInput(addr);
    setValidationError('');
    onInvestigate(addr);
  };

  return (
    <section className="search-section">
      <div className="search-header">
        <h2 className="section-title">🔍 Wallet Intelligence Target</h2>
        <span className="search-subtitle">Enter an on-chain address for multi-hop risk &amp; VASP attribution</span>
      </div>

      <form onSubmit={handleSubmit} className="search-bar-row">
        <div className="input-wrapper">
          <span className="input-icon">⛓️</span>
          <input
            type="text"
            className={`wallet-input ${validationError ? 'input-error' : ''}`}
            value={addressInput}
            onChange={handleChange}
            placeholder="Enter Ethereum wallet address (0x...)"
            disabled={isLoading}
            spellCheck="false"
          />
          {addressInput && (
            <button
              type="button"
              className="btn-clear"
              onClick={() => {
                setAddressInput('');
                setValidationError('');
              }}
              title="Clear input"
            >
              ✕
            </button>
          )}
        </div>

        <button type="submit" className="btn-primary" disabled={isLoading}>
          {isLoading ? (
            <span className="btn-spinner-text">
              <span className="spinner-icon"></span> Analyzing...
            </span>
          ) : (
            'Investigate Wallet'
          )}
        </button>
      </form>

      {validationError && (
        <div className="input-warning-msg">
          <span>⚠️</span> {validationError}
        </div>
      )}

      <div className="demo-chips">
        <span className="demo-label">Quick Load Test Targets:</span>
        <button
          type="button"
          className="chip-btn chip-danger"
          onClick={() => handleChipClick(DEMO_ADDRESSES.SCAM)}
          disabled={isLoading}
        >
          🚨 Scam Target (High Risk)
        </button>
        <button
          type="button"
          className="chip-btn chip-info"
          onClick={() => handleChipClick(DEMO_ADDRESSES.VICTIM)}
          disabled={isLoading}
        >
          🛡️ Victim Origin
        </button>
        <button
          type="button"
          className="chip-btn chip-vasp"
          onClick={() => handleChipClick(DEMO_ADDRESSES.VASP_APEX)}
          disabled={isLoading}
        >
          🏛️ ApexExchange (VASP)
        </button>
      </div>
    </section>
  );
}
