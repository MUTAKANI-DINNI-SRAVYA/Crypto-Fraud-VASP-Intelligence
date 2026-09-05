import React, { useEffect, useRef, useState, useMemo } from 'react';
import cytoscape from 'cytoscape';
import './MoneyFlowGraph.css';

/**
 * Shorten wallet address for display label
 */
const shortenAddress = (addr) => {
  if (!addr) return '';
  if (addr.length <= 12) return addr;
  return `${addr.substring(0, 6)}...${addr.substring(addr.length - 4)}`;
};

/**
 * Determine node category for styling
 */
const getNodeCategory = (addr) => {
  const lower = (addr || '').toLowerCase();
  if (lower.includes('vic')) return 'victim';
  if (lower.includes('scam')) return 'scam';
  if (lower.includes('vasp')) return 'vasp';
  return 'relayer';
};

export default function MoneyFlowGraph({ transactions = [] }) {
  const containerRef = useRef(null);
  const cyRef = useRef(null);
  const [selectedNodeData, setSelectedNodeData] = useState(null);
  const [selectedEdgeData, setSelectedEdgeData] = useState(null);

  // Derive Cytoscape elements from transactions prop
  const elements = useMemo(() => {
    if (!transactions || !Array.isArray(transactions) || transactions.length === 0) {
      return [];
    }

    const uniqueNodes = new Map();
    const edgeList = [];

    transactions.forEach((tx, idx) => {
      if (!tx.from || !tx.to) return;

      // Add 'from' node if not present
      if (!uniqueNodes.has(tx.from)) {
        uniqueNodes.set(tx.from, {
          data: {
            id: tx.from,
            label: shortenAddress(tx.from),
            fullAddress: tx.from,
            category: getNodeCategory(tx.from)
          }
        });
      }

      // Add 'to' node if not present
      if (!uniqueNodes.has(tx.to)) {
        uniqueNodes.set(tx.to, {
          data: {
            id: tx.to,
            label: shortenAddress(tx.to),
            fullAddress: tx.to,
            category: getNodeCategory(tx.to)
          }
        });
      }

      // Add directed edge
      const edgeId = tx.hash || `tx-${idx}-${tx.from}-${tx.to}`;
      edgeList.push({
        data: {
          id: edgeId,
          source: tx.from,
          target: tx.to,
          amount: tx.amount,
          asset: tx.asset || 'ETH',
          label: `${tx.amount} ${tx.asset || 'ETH'}`,
          timestamp: tx.timestamp,
          hash: tx.hash
        }
      });
    });

    return [...Array.from(uniqueNodes.values()), ...edgeList];
  }, [transactions]);

  // Handle selected node statistical computation
  const computeNodeStats = (address) => {
    if (!address || !transactions) return null;

    let inflowSum = 0;
    let outflowSum = 0;
    const connectedTxs = [];

    transactions.forEach((tx) => {
      if (tx.from === address) {
        outflowSum += Number(tx.amount || 0);
        connectedTxs.push({ ...tx, direction: 'OUT' });
      }
      if (tx.to === address) {
        inflowSum += Number(tx.amount || 0);
        connectedTxs.push({ ...tx, direction: 'IN' });
      }
    });

    return {
      address,
      category: getNodeCategory(address),
      inflow: inflowSum,
      outflow: outflowSum,
      connectedTxs
    };
  };

  // Cytoscape initialization & updates
  useEffect(() => {
    if (!containerRef.current || elements.length === 0) {
      if (cyRef.current) {
        cyRef.current.destroy();
        cyRef.current = null;
      }
      return;
    }

    // Initialize Cytoscape
    const cy = cytoscape({
      container: containerRef.current,
      elements: elements,
      boxSelectionEnabled: false,
      autounselectify: false,
      style: [
        {
          selector: 'node',
          style: {
            'shape': 'ellipse',
            'width': 42,
            'height': 42,
            'background-color': '#38bdf8',
            'border-width': 2,
            'border-color': '#0b0f19',
            'label': 'data(label)',
            'color': '#f8fafc',
            'font-size': '11px',
            'font-family': 'monospace',
            'font-weight': 600,
            'text-valign': 'bottom',
            'text-margin-y': 6,
            'transition-property': 'background-color, border-width, border-color',
            'transition-duration': '0.15s'
          }
        },
        {
          selector: 'node[category = "victim"]',
          style: {
            'background-color': '#10b981',
            'border-color': '#065f46'
          }
        },
        {
          selector: 'node[category = "scam"]',
          style: {
            'background-color': '#f43f5e',
            'border-color': '#881337'
          }
        },
        {
          selector: 'node[category = "vasp"]',
          style: {
            'background-color': '#f59e0b',
            'border-color': '#78350f'
          }
        },
        {
          selector: 'node:selected',
          style: {
            'border-width': 4,
            'border-color': '#ffffff',
            'shadow-blur': 12,
            'shadow-color': '#38bdf8',
            'shadow-opacity': 0.8
          }
        },
        {
          selector: 'edge',
          style: {
            'width': 2.5,
            'line-color': '#3b82f6',
            'target-arrow-color': '#3b82f6',
            'target-arrow-shape': 'triangle',
            'arrow-scale': 1.2,
            'curve-style': 'bezier',
            'label': 'data(label)',
            'font-size': '10px',
            'font-family': 'monospace',
            'color': '#cbd5e1',
            'text-rotation': 'autorotate',
            'text-margin-y': -8,
            'text-background-opacity': 0.85,
            'text-background-color': '#0b0f19',
            'text-background-padding': '3px',
            'text-background-shape': 'roundrect'
          }
        },
        {
          selector: 'edge:selected',
          style: {
            'width': 3.5,
            'line-color': '#38bdf8',
            'target-arrow-color': '#38bdf8'
          }
        }
      ],
      layout: {
        name: 'breadthfirst',
        directed: true,
        padding: 40,
        spacingFactor: 1.35,
        animate: true,
        animationDuration: 400
      }
    });

    // Node click handler
    cy.on('tap', 'node', (evt) => {
      const node = evt.target;
      const address = node.data('fullAddress');
      const stats = computeNodeStats(address);
      setSelectedNodeData(stats);
      setSelectedEdgeData(null);
    });

    // Edge click handler
    cy.on('tap', 'edge', (evt) => {
      const edge = evt.target;
      setSelectedEdgeData(edge.data());
      setSelectedNodeData(null);
    });

    // Background click handler to clear selection
    cy.on('tap', (evt) => {
      if (evt.target === cy) {
        setSelectedNodeData(null);
        setSelectedEdgeData(null);
      }
    });

    cyRef.current = cy;

    // Cleanup on unmount / re-render
    return () => {
      if (cyRef.current) {
        cyRef.current.destroy();
        cyRef.current = null;
      }
    };
  }, [elements]);

  // Canvas Control Handlers
  const handleFit = () => {
    cyRef.current?.fit(undefined, 30);
  };

  const handleResetLayout = () => {
    if (!cyRef.current) return;
    cyRef.current.layout({
      name: 'breadthfirst',
      directed: true,
      padding: 40,
      spacingFactor: 1.35,
      animate: true,
      animationDuration: 300
    }).run();
  };

  const handleZoomIn = () => {
    if (!cyRef.current) return;
    cyRef.current.zoom({
      level: cyRef.current.zoom() * 1.25,
      position: { x: cyRef.current.width() / 2, y: cyRef.current.height() / 2 }
    });
  };

  const handleZoomOut = () => {
    if (!cyRef.current) return;
    cyRef.current.zoom({
      level: cyRef.current.zoom() * 0.8,
      position: { x: cyRef.current.width() / 2, y: cyRef.current.height() / 2 }
    });
  };

  // Render Empty State if no transactions
  if (!transactions || transactions.length === 0) {
    return (
      <div className="graph-empty-state">
        <div className="empty-icon">🕸️</div>
        <p style={{ fontWeight: 600, color: 'var(--text-primary, #f8fafc)' }}>
          No Transaction Data Available
        </p>
        <p style={{ fontSize: '0.85rem' }}>
          Provide an array of transactions to visualize money flows.
        </p>
      </div>
    );
  }

  return (
    <div className="money-flow-graph-container">
      {/* Cytoscape Canvas Area */}
      <div className="cy-canvas-wrapper">
        {/* Floating Toolbar */}
        <div className="graph-toolbar">
          <button className="graph-tool-btn" onClick={handleFit} title="Fit graph view">
            🎯 Fit
          </button>
          <button className="graph-tool-btn" onClick={handleResetLayout} title="Reset layout">
            🔄 Layout
          </button>
          <button className="graph-tool-btn" onClick={handleZoomIn} title="Zoom In">
            ➕
          </button>
          <button className="graph-tool-btn" onClick={handleZoomOut} title="Zoom Out">
            ➖
          </button>
        </div>

        {/* Legend Overlay */}
        <div className="graph-legend">
          <div className="legend-item">
            <span className="legend-dot victim"></span> Victim
          </div>
          <div className="legend-item">
            <span className="legend-dot scam"></span> Scam
          </div>
          <div className="legend-item">
            <span className="legend-dot relayer"></span> Relayer
          </div>
          <div className="legend-item">
            <span className="legend-dot vasp"></span> VASP
          </div>
        </div>

        <div ref={containerRef} className="cy-canvas" />
      </div>

      {/* Selected Node Details Side Panel / Drawer */}
      {selectedNodeData && (
        <div className="node-details-panel">
          <div className="node-details-header">
            <div className="node-details-title">
              <span>📍 Selected Node Details</span>
              <span className="node-address-badge">{selectedNodeData.address}</span>
            </div>
            <button className="close-panel-btn" onClick={() => setSelectedNodeData(null)}>
              ✕
            </button>
          </div>

          <div className="node-stats-grid">
            <div className="stat-box">
              <div className="stat-label">Category</div>
              <div className="stat-value" style={{ textTransform: 'capitalize' }}>
                {selectedNodeData.category}
              </div>
            </div>
            <div className="stat-box">
              <div className="stat-label">Total Inflow</div>
              <div className="stat-value inflow">+{selectedNodeData.inflow.toFixed(2)} ETH</div>
            </div>
            <div className="stat-box">
              <div className="stat-label">Total Outflow</div>
              <div className="stat-value outflow">-{selectedNodeData.outflow.toFixed(2)} ETH</div>
            </div>
          </div>

          <div style={{ marginTop: '0.25rem' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>
              Connected Transactions ({selectedNodeData.connectedTxs.length})
            </div>
            <div className="node-tx-list">
              {selectedNodeData.connectedTxs.map((tx, idx) => (
                <div key={idx} className="node-tx-item">
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span className={`node-tx-dir ${tx.direction.toLowerCase()}`}>
                      {tx.direction}
                    </span>
                    <span>{shortenAddress(tx.direction === 'IN' ? tx.from : tx.to)}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Selected Edge Details Panel */}
      {selectedEdgeData && (
        <div className="node-details-panel">
          <div className="node-details-header">
            <div className="node-details-title">
              <span>🔗 Transaction Edge Details</span>
              <span className="node-address-badge">{shortenAddress(selectedEdgeData.hash || selectedEdgeData.id)}</span>
            </div>
            <button className="close-panel-btn" onClick={() => setSelectedEdgeData(null)}>
              ✕
            </button>
          </div>

          <div className="node-stats-grid">
            <div className="stat-box">
              <div className="stat-label">From Address</div>
              <div className="stat-value" style={{ fontSize: '0.8rem', color: 'var(--accent-cyan)' }}>
                {shortenAddress(selectedEdgeData.source)}
              </div>
            </div>
            <div className="stat-box">
              <div className="stat-label">To Address</div>
              <div className="stat-value" style={{ fontSize: '0.8rem', color: 'var(--accent-cyan)' }}>
                {shortenAddress(selectedEdgeData.target)}
              </div>
            </div>
            <div className="stat-box">
              <div className="stat-label">Amount Transferred</div>
              <div className="stat-value inflow">
                {selectedEdgeData.amount} {selectedEdgeData.asset || 'ETH'}
              </div>
            </div>
            <div className="stat-box">
              <div className="stat-label">Timestamp</div>
              <div className="stat-value" style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                {selectedEdgeData.timestamp ? new Date(selectedEdgeData.timestamp).toUTCString() : 'N/A'}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
