import React, { useEffect, useRef, useState } from 'react';
import cytoscape from 'cytoscape';

export default function MoneyFlowGraph({ graphData, targetAddress }) {
  const containerRef = useRef(null);
  const cyRef = useRef(null);
  const [selectedElement, setSelectedElement] = useState(null);
  const [layoutName, setLayoutName] = useState('breadthfirst');

  useEffect(() => {
    if (!containerRef.current) return;

    // Build elements array for Cytoscape
    const elements = [];

    if (graphData?.nodes && Array.isArray(graphData.nodes)) {
      graphData.nodes.forEach((n) => {
        elements.push({
          group: 'nodes',
          data: {
            ...n.data,
            id: n.data.id,
            label: n.data.label || n.data.shortId || n.data.id?.substring(0, 8),
            type: n.data.type || 'hop',
          },
          classes: n.data.type || 'hop',
        });
      });
    }

    if (graphData?.edges && Array.isArray(graphData.edges)) {
      graphData.edges.forEach((e) => {
        elements.push({
          group: 'edges',
          data: {
            ...e.data,
            id: e.data.id,
            source: e.data.source,
            target: e.data.target,
            label: e.data.label || `${e.data.amount || ''} ${e.data.asset || 'ETH'}`,
          },
        });
      });
    }

    // Initialize Cytoscape
    try {
      if (cyRef.current) {
        cyRef.current.destroy();
      }

      const cy = cytoscape({
        container: containerRef.current,
        elements,
        style: [
          {
            selector: 'node',
            style: {
              label: 'data(label)',
              color: '#f8fafc',
              'font-size': '11px',
              'font-family': 'Inter, sans-serif',
              'text-valign': 'bottom',
              'text-margin-y': 6,
              'background-color': '#3b82f6',
              width: 38,
              height: 38,
              'border-width': 2,
              'border-color': '#1e293b',
              'text-outline-color': '#0b0f19',
              'text-outline-width': 2,
              'transition-property': 'background-color, border-color, width, height',
              'transition-duration': '0.2s',
            },
          },
          // Node styles by role/type
          {
            selector: 'node.victim',
            style: {
              'background-color': '#10b981', // Green
              'border-color': '#34d399',
            },
          },
          {
            selector: 'node.scam',
            style: {
              'background-color': '#f43f5e', // Red/Rose
              'border-color': '#fb7185',
              width: 46,
              height: 46,
            },
          },
          {
            selector: 'node.hop',
            style: {
              'background-color': '#f59e0b', // Amber/Orange
              'border-color': '#fbbf24',
            },
          },
          {
            selector: 'node.vasp',
            style: {
              'background-color': '#8b5cf6', // Purple/VASP
              'border-color': '#c084fc',
              shape: 'diamond',
              width: 44,
              height: 44,
            },
          },
          {
            selector: 'node:selected',
            style: {
              'border-width': 4,
              'border-color': '#38bdf8',
              'shadow-blur': 15,
              'shadow-color': '#38bdf8',
              'shadow-opacity': 0.8,
            },
          },
          // Edge styling
          {
            selector: 'edge',
            style: {
              label: 'data(label)',
              'font-size': '10px',
              'font-family': 'JetBrains Mono, monospace',
              color: '#94a3b8',
              width: 2,
              'line-color': '#334155',
              'target-arrow-color': '#38bdf8',
              'target-arrow-shape': 'triangle',
              'curve-style': 'bezier',
              'arrow-scale': 1.2,
              'text-background-color': '#0b0f19',
              'text-background-opacity': 0.85,
              'text-background-padding': '3px',
              'text-background-shape': 'roundrectangle',
            },
          },
          {
            selector: 'edge:selected',
            style: {
              width: 3,
              'line-color': '#38bdf8',
              'target-arrow-color': '#38bdf8',
            },
          },
        ],
        layout: {
          name: layoutName,
          directed: true,
          padding: 30,
          spacingFactor: 1.25,
          animate: true,
          animationDuration: 400,
        },
      });

      // Event listeners
      cy.on('tap', 'node', (evt) => {
        const node = evt.target;
        setSelectedElement({
          category: 'node',
          id: node.data('id'),
          label: node.data('label'),
          type: node.data('type'),
          balance: node.data('balance'),
          isLastTraceablePoint: node.data('isLastTraceablePoint'),
          categoryName: node.data('category'),
        });
      });

      cy.on('tap', 'edge', (evt) => {
        const edge = evt.target;
        setSelectedElement({
          category: 'edge',
          id: edge.data('id'),
          source: edge.data('source'),
          target: edge.data('target'),
          amount: edge.data('amount'),
          asset: edge.data('asset'),
          timestamp: edge.data('timestamp'),
        });
      });

      cy.on('tap', (evt) => {
        if (evt.target === cy) {
          setSelectedElement(null);
        }
      });

      cyRef.current = cy;
    } catch (err) {
      console.error('Cytoscape graph rendering failed:', err);
    }

    return () => {
      if (cyRef.current) {
        cyRef.current.destroy();
        cyRef.current = null;
      }
    };
  }, [graphData, layoutName]);

  const handleFit = () => {
    if (cyRef.current) {
      cyRef.current.fit(undefined, 30);
    }
  };

  const handleResetZoom = () => {
    if (cyRef.current) {
      cyRef.current.reset();
      cyRef.current.center();
    }
  };

  return (
    <div className="card graph-card-wrapper">
      <div className="card-header-flex">
        <div className="graph-header-left">
          <h2 className="card-title">
            <span className="card-icon">🕸️</span>
            Money Flow Graph Visualization (Cytoscape.js)
          </h2>
          <span className="graph-subtitle">
            Interactive multi-hop directed chain of custody
          </span>
        </div>

        <div className="graph-controls-row">
          <div className="layout-select-group">
            <label htmlFor="graph-layout">Layout:</label>
            <select
              id="graph-layout"
              className="select-input"
              value={layoutName}
              onChange={(e) => setLayoutName(e.target.value)}
            >
              <option value="breadthfirst">Flow (Hierarchy)</option>
              <option value="concentric">Concentric Rings</option>
              <option value="circle">Circular</option>
              <option value="grid">Grid</option>
            </select>
          </div>

          <button type="button" className="btn-secondary" onClick={handleFit} title="Fit to viewport">
            Fit Canvas
          </button>
          <button type="button" className="btn-secondary" onClick={handleResetZoom} title="Reset zoom">
            Reset
          </button>
        </div>
      </div>

      {/* Legend */}
      <div className="graph-legend">
        <div className="legend-item">
          <span className="legend-dot dot-victim"></span>
          <span>Victim Wallet</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot dot-scam"></span>
          <span>Scam Target</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot dot-hop"></span>
          <span>Intermediary Hops</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot dot-vasp"></span>
          <span>VASP (Last Traceable Point)</span>
        </div>
      </div>

      {/* Canvas container */}
      <div className="graph-viewport-relative">
        <div ref={containerRef} className="cytoscape-canvas"></div>

        {/* Floating node/edge inspector */}
        {selectedElement && (
          <div className="graph-inspector-panel">
            <div className="inspector-header">
              <strong>
                {selectedElement.category === 'node' ? 'Node Intelligence' : 'Transaction Edge'}
              </strong>
              <button
                type="button"
                className="btn-close-sm"
                onClick={() => setSelectedElement(null)}
              >
                ✕
              </button>
            </div>

            {selectedElement.category === 'node' ? (
              <div className="inspector-content">
                <div className="inspector-row">
                  <span className="label">Entity Label:</span>
                  <span className="value font-bold">{selectedElement.label}</span>
                </div>
                <div className="inspector-row">
                  <span className="label">Role / Type:</span>
                  <span className={`badge-pill badge-${selectedElement.type}`}>
                    {(selectedElement.type || '').toUpperCase()}
                  </span>
                </div>
                <div className="inspector-row">
                  <span className="label">Address:</span>
                  <span className="value mono break-all">{selectedElement.id}</span>
                </div>
                {selectedElement.balance && (
                  <div className="inspector-row">
                    <span className="label">Estimated Balance:</span>
                    <span className="value">{selectedElement.balance}</span>
                  </div>
                )}
                {selectedElement.isLastTraceablePoint && (
                  <div className="last-point-tag">
                    🚨 LAST TRACEABLE POINT — Custodial Boundary
                  </div>
                )}
              </div>
            ) : (
              <div className="inspector-content">
                <div className="inspector-row">
                  <span className="label">Amount:</span>
                  <span className="value text-emerald font-bold">
                    {selectedElement.amount} {selectedElement.asset || 'ETH'}
                  </span>
                </div>
                <div className="inspector-row">
                  <span className="label">From:</span>
                  <span className="value mono">{selectedElement.source?.substring(0, 10)}...</span>
                </div>
                <div className="inspector-row">
                  <span className="label">To:</span>
                  <span className="value mono">{selectedElement.target?.substring(0, 10)}...</span>
                </div>
                {selectedElement.timestamp && (
                  <div className="inspector-row">
                    <span className="label">Time:</span>
                    <span className="value">{selectedElement.timestamp}</span>
                  </div>
                )}
              </div>
            )}
          </div>
        )}
      </div>

      <div className="graph-footer-hint">
        <span>💡 Hint: Drag nodes to rearrange • Scroll to zoom • Click nodes or edges to inspect intelligence</span>
      </div>
    </div>
  );
}
