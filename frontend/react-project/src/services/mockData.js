/**
 * Mock Intelligence Dataset for Crypto Fraud & VASP Intelligence
 * Strictly adheres to docs/api-contract.md and data/sample-*.json.
 * Used as automatic fallback when Spring Boot backend is offline.
 */

export const DEMO_ADDRESSES = {
  SCAM: '0xSCAM999999999999999999999999999999999999',
  VICTIM: '0xVIC1111111111111111111111111111111111111',
  VASP_APEX: '0xVASP888888888888888888888888888888888888',
  VASP_NOVAPAY: '0xVASP777777777777777777777777777777777777',
};

export const MOCK_TRANSACTIONS = [
  {
    hash: '0x1000000000000000000000000000000000000000000000000000000000000001',
    from: '0xVIC1111111111111111111111111111111111111',
    to: '0xSCAM999999999999999999999999999999999999',
    amount: 5.0,
    asset: 'ETH',
    timestamp: '2026-09-02T10:00:00Z',
    direction: 'IN',
  },
  {
    hash: '0x2000000000000000000000000000000000000000000000000000000000000002',
    from: '0xSCAM999999999999999999999999999999999999',
    to: '0xAAAA111111111111111111111111111111111111',
    amount: 2.0,
    asset: 'ETH',
    timestamp: '2026-09-02T10:05:00Z',
    direction: 'OUT',
  },
  {
    hash: '0x3000000000000000000000000000000000000000000000000000000000000003',
    from: '0xSCAM999999999999999999999999999999999999',
    to: '0xBBBB222222222222222222222222222222222222',
    amount: 1.5,
    asset: 'ETH',
    timestamp: '2026-09-02T10:06:00Z',
    direction: 'OUT',
  },
  {
    hash: '0x4000000000000000000000000000000000000000000000000000000000000004',
    from: '0xSCAM999999999999999999999999999999999999',
    to: '0xCCCC333333333333333333333333333333333333',
    amount: 1.5,
    asset: 'ETH',
    timestamp: '2026-09-02T10:07:00Z',
    direction: 'OUT',
  },
  {
    hash: '0x5000000000000000000000000000000000000000000000000000000000000005',
    from: '0xAAAA111111111111111111111111111111111111',
    to: '0xDDDD444444444444444444444444444444444444',
    amount: 1.98,
    asset: 'ETH',
    timestamp: '2026-09-02T10:14:00Z',
    direction: 'HOP',
  },
  {
    hash: '0x6000000000000000000000000000000000000000000000000000000000000006',
    from: '0xDDDD444444444444444444444444444444444444',
    to: '0xVASP888888888888888888888888888888888888',
    amount: 1.95,
    asset: 'ETH',
    timestamp: '2026-09-02T10:22:00Z',
    direction: 'TERMINUS',
  },
  {
    hash: '0x7000000000000000000000000000000000000000000000000000000000000007',
    from: '0xBBBB222222222222222222222222222222222222',
    to: '0xVASP777777777777777777777777777777777777',
    amount: 1.48,
    asset: 'ETH',
    timestamp: '2026-09-02T10:18:00Z',
    direction: 'TERMINUS',
  },
  {
    hash: '0x8000000000000000000000000000000000000000000000000000000000000008',
    from: '0xCCCC333333333333333333333333333333333333',
    to: '0xEEEE555555555555555555555555555555555555',
    amount: 1.48,
    asset: 'ETH',
    timestamp: '2026-09-02T10:35:00Z',
    direction: 'HOP',
  },
  {
    hash: '0x9000000000000000000000000000000000000000000000000000000000000009',
    from: '0xEEEE555555555555555555555555555555555555',
    to: '0xHOLD666666666666666666666666666666666666',
    amount: 1.45,
    asset: 'ETH',
    timestamp: '2026-09-02T11:00:00Z',
    direction: 'HOP',
  },
];

export const MOCK_WALLET_SUMMARY = {
  address: '0xSCAM999999999999999999999999999999999999',
  balance: 0.0,
  asset: 'ETH',
  totalReceived: 5.0,
  totalSent: 5.0,
  transactionCount: 4,
  hopCount: 3,
  firstActive: '2026-09-02T10:00:00Z',
  lastActive: '2026-09-02T10:07:00Z',
};

export const MOCK_RISK_RESULT = {
  address: '0xSCAM999999999999999999999999999999999999',
  riskScore: 85,
  riskLevel: 'HIGH',
  evaluatedAt: '2026-09-02T21:50:00Z',
  disclaimer:
    'Prototype heuristic score for demonstration purposes only — not an AML compliance determination.',
  triggeredRules: [
    {
      ruleId: 'RULE_FUND_SPLITTING',
      ruleName: 'Fund Splitting Pattern',
      scoreDelta: 25,
      description:
        'Incoming funds were divided into 3 or more recipient addresses within 7 minutes.',
    },
    {
      ruleId: 'RULE_RAPID_MOVEMENT',
      ruleName: 'Rapid Asset Relayering',
      scoreDelta: 25,
      description:
        'Transfers dispatched in under 15 minutes from deposit receipt.',
    },
    {
      ruleId: 'RULE_MULTI_HOP',
      ruleName: 'Multi-Hop Layering Flow',
      scoreDelta: 20,
      description:
        'Chain of custody extends through 3 intermediate hops to obscure origin.',
    },
    {
      ruleId: 'RULE_VASP_DEPOSIT',
      ruleName: 'Custodial VASP Deposit Termination',
      scoreDelta: 15,
      description:
        'Fund branches terminate directly at custodial exchange deposit addresses.',
    },
  ],
};

export const MOCK_GRAPH_DATA = {
  targetAddress: '0xSCAM999999999999999999999999999999999999',
  nodes: [
    {
      data: {
        id: '0xVIC1111111111111111111111111111111111111',
        label: 'Victim Wallet',
        shortId: '0xVIC1...1111',
        type: 'victim',
        balance: '0.05 ETH',
      },
    },
    {
      data: {
        id: '0xSCAM999999999999999999999999999999999999',
        label: 'Target Scam Wallet',
        shortId: '0xSCAM...9999',
        type: 'scam',
        balance: '0.00 ETH',
      },
    },
    {
      data: {
        id: '0xAAAA111111111111111111111111111111111111',
        label: 'Intermediary A',
        shortId: '0xAAAA...1111',
        type: 'hop',
        balance: '0.02 ETH',
      },
    },
    {
      data: {
        id: '0xBBBB222222222222222222222222222222222222',
        label: 'Intermediary B',
        shortId: '0xBBBB...2222',
        type: 'hop',
        balance: '0.02 ETH',
      },
    },
    {
      data: {
        id: '0xCCCC333333333333333333333333333333333333',
        label: 'Intermediary C',
        shortId: '0xCCCC...3333',
        type: 'hop',
        balance: '0.02 ETH',
      },
    },
    {
      data: {
        id: '0xDDDD444444444444444444444444444444444444',
        label: 'Hop D (Pre-VASP)',
        shortId: '0xDDDD...4444',
        type: 'hop',
        balance: '0.03 ETH',
      },
    },
    {
      data: {
        id: '0xVASP888888888888888888888888888888888888',
        label: 'ApexExchange (VASP)',
        shortId: '0xVASP...8888',
        type: 'vasp',
        isLastTraceablePoint: true,
        category: 'Centralized Exchange',
      },
    },
    {
      data: {
        id: '0xVASP777777777777777777777777777777777777',
        label: 'NovaPay Crypto (VASP)',
        shortId: '0xVASP...7777',
        type: 'vasp',
        isLastTraceablePoint: true,
        category: 'Payment Processor',
      },
    },
  ],
  edges: [
    {
      data: {
        id: 'e1',
        source: '0xVIC1111111111111111111111111111111111111',
        target: '0xSCAM999999999999999999999999999999999999',
        label: '5.00 ETH',
        amount: 5.0,
        asset: 'ETH',
        timestamp: '10:00:00Z',
      },
    },
    {
      data: {
        id: 'e2',
        source: '0xSCAM999999999999999999999999999999999999',
        target: '0xAAAA111111111111111111111111111111111111',
        label: '2.00 ETH',
        amount: 2.0,
        asset: 'ETH',
        timestamp: '10:05:00Z',
      },
    },
    {
      data: {
        id: 'e3',
        source: '0xSCAM999999999999999999999999999999999999',
        target: '0xBBBB222222222222222222222222222222222222',
        label: '1.50 ETH',
        amount: 1.5,
        asset: 'ETH',
        timestamp: '10:06:00Z',
      },
    },
    {
      data: {
        id: 'e4',
        source: '0xSCAM999999999999999999999999999999999999',
        target: '0xCCCC333333333333333333333333333333333333',
        label: '1.50 ETH',
        amount: 1.5,
        asset: 'ETH',
        timestamp: '10:07:00Z',
      },
    },
    {
      data: {
        id: 'e5',
        source: '0xAAAA111111111111111111111111111111111111',
        target: '0xDDDD444444444444444444444444444444444444',
        label: '1.98 ETH',
        amount: 1.98,
        asset: 'ETH',
        timestamp: '10:14:00Z',
      },
    },
    {
      data: {
        id: 'e6',
        source: '0xDDDD444444444444444444444444444444444444',
        target: '0xVASP888888888888888888888888888888888888',
        label: '1.95 ETH',
        amount: 1.95,
        asset: 'ETH',
        timestamp: '10:22:00Z',
      },
    },
    {
      data: {
        id: 'e7',
        source: '0xBBBB222222222222222222222222222222222222',
        target: '0xVASP777777777777777777777777777777777777',
        label: '1.48 ETH',
        amount: 1.48,
        asset: 'ETH',
        timestamp: '10:18:00Z',
      },
    },
  ],
};

export const MOCK_VASP_FINDINGS = [
  {
    address: '0xVASP888888888888888888888888888888888888',
    vaspName: 'ApexExchange (Fictional Demo VASP)',
    category: 'Centralized Exchange (CEX)',
    custodialType: 'Omnibus Deposit Hot Wallet',
    country: 'Demo Island Regulatory Zone',
    isLastTraceablePoint: true,
    interactionStatus: 'LAST_TRACEABLE_POINT',
    amountReceived: 1.95,
    asset: 'ETH',
    hopDistance: 3,
    boundaryNotice:
      'LAST TRACEABLE POINT: Further lawful off-chain records required. Funds entered custodial exchange omnibus pool.',
    complianceNotice:
      'Off-chain internal ledger accounts and customer KYC require appropriate legal subpoena or LEA request.',
  },
  {
    address: '0xVASP777777777777777777777777777777777777',
    vaspName: 'NovaPay Crypto (Fictional Demo VASP)',
    category: 'Crypto Payment Processor / Gateway',
    custodialType: 'Merchant Processing Pool',
    country: 'Demo Metropolis Gateway',
    isLastTraceablePoint: true,
    interactionStatus: 'LAST_TRACEABLE_POINT',
    amountReceived: 1.48,
    asset: 'ETH',
    hopDistance: 2,
    boundaryNotice:
      'LAST TRACEABLE POINT: Further lawful off-chain records required. Funds converted to merchant settlement credits off-chain.',
    complianceNotice:
      'Funds converted to merchant settlement credits off-chain. Subpoena needed for merchant account holder identity.',
  },
];

export const MOCK_AI_EXPLANATION = {
  suspiciousBehavior:
    'Target wallet exhibited high-speed fund splitting and rapid asset movement. Immediately following the deposit of 5.00 ETH from the reported victim wallet, 100% of the funds were dispersed across three distinct downstream addresses in under 7 minutes, a behavior characteristic of automated layering syndicates.',
  investigationSummary:
    'Branch A routed through an intermediary hop (0xDDDD...4444) before depositing 1.95 ETH into ApexExchange. Branch B transferred 1.48 ETH into NovaPay Crypto. Branch C continues hopping across unhosted wallets. The public ledger trail ceases at the designated VASP endpoints.',
  importantFindings: [
    '5.00 ETH ingested from victim 0xVIC1... at 10:00:00Z',
    'Dispersed into 3 hops within 7 minutes (avg. latency: 2.3 min/tx)',
    '1.95 ETH deposited into ApexExchange custodial pool (Last Traceable Point 1)',
    '1.48 ETH deposited into NovaPay payment processor (Last Traceable Point 2)',
    '0 ETH balance remains on target wallet',
  ],
  handoverNotes:
    'Public ledger tracing is exhausted at the VASP boundary. On-chain records cannot reveal customer identity or fiat withdrawal details. Formal legal request / 2703(d) order required for ApexExchange and NovaPay internal accounting ledgers.',
  isFallback: true,
  modelName: 'Gemini 1.5 Pro (Simulated / Contract Format)',
};

export const MOCK_INVESTIGATION_REPORT = {
  reportId: 'IR-20260902-DEMO-001',
  generatedAt: new Date().toISOString(),
  caseStatus: 'PRELIMINARY_TRIAGE',
  investigator: 'Security Analyst (Renuka - Member 5 Dashboard)',
  targetAddress: '0xSCAM999999999999999999999999999999999999',
  walletSummary: MOCK_WALLET_SUMMARY,
  riskEvaluation: MOCK_RISK_RESULT,
  vaspFindings: MOCK_VASP_FINDINGS,
  aiExecutiveSummary:
    'PRELIMINARY ON-CHAIN BRIEFING: The analyzed wallet acts as a high-velocity transit hub. Following receipt of 5.00 ETH from victim wallet 0xVIC1111..., funds were split across 3 downstream branches within 7 minutes. Branch A traversed through intermediary wallet 0xDDDD4444... before terminating at ApexExchange deposit wallet (1.95 ETH). Branch B routed directly into NovaPay Crypto (1.48 ETH). Public ledger visibility ceases at these two VASP boundaries.',
  disclaimers: {
    heuristicDisclaimer:
      'This score and analysis is an automated prototype heuristic indicator intended for investigative triage. It is not an AML compliance determination or proof of illegal conduct.',
    onChainLimitation:
      'Blockchain records do not contain customer identities, bank account details, or cash withdrawal data. The designated endpoints represent the Last Traceable Points on the public ledger.',
    fictionalData:
      'All VASP names, addresses, and transaction amounts are fictional demonstration data for hackathon evaluation.',
  },
};

/**
 * Returns complete aggregated investigation data for a given wallet address.
 * Generates dynamic fallback data if address doesn't match default demo.
 */
export function getMockInvestigation(address) {
  const isScam =
    !address ||
    address.toLowerCase() === DEMO_ADDRESSES.SCAM.toLowerCase() ||
    address.toLowerCase().includes('scam');
  const isVictim = address && address.toLowerCase().includes('vic');
  const isVasp = address && address.toLowerCase().includes('vasp');

  if (isVictim) {
    return {
      targetAddress: address,
      summary: {
        address,
        balance: 0.05,
        asset: 'ETH',
        totalReceived: 10.0,
        totalSent: 5.0,
        transactionCount: 2,
        hopCount: 1,
        firstActive: '2026-09-01T09:00:00Z',
        lastActive: '2026-09-02T10:00:00Z',
      },
      risk: {
        address,
        riskScore: 15,
        riskLevel: 'LOW',
        evaluatedAt: new Date().toISOString(),
        disclaimer: MOCK_RISK_RESULT.disclaimer,
        triggeredRules: [
          {
            ruleId: 'RULE_NORMAL_HOLDING',
            ruleName: 'Standard Holding Period',
            scoreDelta: 15,
            description: 'Funds held for extended period before single transfer.',
          },
        ],
      },
      transactions: [MOCK_TRANSACTIONS[0]],
      graphData: {
        targetAddress: address,
        nodes: [MOCK_GRAPH_DATA.nodes[0], MOCK_GRAPH_DATA.nodes[1]],
        edges: [MOCK_GRAPH_DATA.edges[0]],
      },
      vaspFindings: [],
      aiExplanation: {
        suspiciousBehavior: 'Wallet demonstrates typical individual retail user patterns.',
        investigationSummary: 'Victim address initiated a 5.00 ETH transfer to reported scam entity.',
        importantFindings: ['Single outbound 5.00 ETH transfer flagged by user as unauthorized or fraudulent.'],
        handoverNotes: 'Victim statement and transaction hash submitted for triage.',
        isFallback: true,
      },
      report: {
        ...MOCK_INVESTIGATION_REPORT,
        reportId: `IR-${Date.now()}-VIC`,
        targetAddress: address,
      },
    };
  }

  if (isVasp) {
    return {
      targetAddress: address,
      summary: {
        address,
        balance: 1450.2,
        asset: 'ETH',
        totalReceived: 25000.0,
        totalSent: 23549.8,
        transactionCount: 1540,
        hopCount: 0,
        firstActive: '2025-01-01T00:00:00Z',
        lastActive: '2026-09-02T10:22:00Z',
      },
      risk: {
        address,
        riskScore: 35,
        riskLevel: 'MEDIUM',
        evaluatedAt: new Date().toISOString(),
        disclaimer: MOCK_RISK_RESULT.disclaimer,
        triggeredRules: [
          {
            ruleId: 'RULE_VASP_HOT_WALLET',
            ruleName: 'Identified Custodial VASP Entity',
            scoreDelta: 35,
            description: 'Recognized centralized custodial pool for ApexExchange.',
          },
        ],
      },
      transactions: [MOCK_TRANSACTIONS[5]],
      graphData: {
        targetAddress: address,
        nodes: [MOCK_GRAPH_DATA.nodes[5], MOCK_GRAPH_DATA.nodes[6]],
        edges: [MOCK_GRAPH_DATA.edges[5]],
      },
      vaspFindings: [MOCK_VASP_FINDINGS[0]],
      aiExplanation: {
        suspiciousBehavior: 'Custodial exchange hot wallet consolidating deposits from multiple deposit proxies.',
        investigationSummary: 'This wallet represents the Last Traceable Point on public blockchain.',
        importantFindings: ['Custodial omnibus pool; internal off-chain ledger handles user balances.'],
        handoverNotes: 'Law enforcement subpoena required to query KYC and exchange account IDs.',
        isFallback: true,
      },
      report: {
        ...MOCK_INVESTIGATION_REPORT,
        reportId: `IR-${Date.now()}-VASP`,
        targetAddress: address,
      },
    };
  }

  // Default High Risk Scam Case
  return {
    targetAddress: address || DEMO_ADDRESSES.SCAM,
    summary: {
      ...MOCK_WALLET_SUMMARY,
      address: address || DEMO_ADDRESSES.SCAM,
    },
    risk: {
      ...MOCK_RISK_RESULT,
      address: address || DEMO_ADDRESSES.SCAM,
    },
    transactions: MOCK_TRANSACTIONS,
    graphData: MOCK_GRAPH_DATA,
    vaspFindings: MOCK_VASP_FINDINGS,
    aiExplanation: MOCK_AI_EXPLANATION,
    report: {
      ...MOCK_INVESTIGATION_REPORT,
      reportId: `IR-${Date.now()}-SCAM`,
      targetAddress: address || DEMO_ADDRESSES.SCAM,
    },
  };
}
