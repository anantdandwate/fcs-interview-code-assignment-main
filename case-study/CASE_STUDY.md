# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking

**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

- Challenges: cost attribution across shared operations (labor, transport, overhead), late data, inconsistent identifiers across systems.
- Needed data: cost centers, activity drivers (orders, picks, distance, weight), warehouse/store mapping history, accounting cut-off rules.
- Strategy: agree one cost model first (fixed vs variable), clean up master data, and standardize timestamps/timezone.
- Controls: reconciliation reports between operational and finance systems, anomaly alerts for sudden cost spikes.
- Questions I would ask first: how do we allocate shared labor today, and where do finance and operations numbers currently disagree?

## Scenario 2: Cost Optimization Strategies

**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

- Candidate levers: slotting optimization, route optimization, inventory rebalancing, labor scheduling, supplier consolidation.
- Prioritization: impact x effort x risk matrix; start with high-impact, low-risk pilots.
- Implementation: baseline KPIs, run controlled experiment, compare before/after, then scale.
- Expected outcomes: lower cost per order, reduced stockouts, improved fill-rate and on-time delivery.
- Question I would ask first: which cost bucket hurts us most right now (labor, transport, or inventory carrying)?

## Scenario 3: Integration with Financial Systems

**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

- Benefits: single source of truth, faster month-end close, accurate profitability by warehouse/store.
- Integration approach: contract-first APIs/events, idempotent consumers, retry with DLQ, schema versioning.
- Data quality: unique business keys, validation on ingress, reconciliation jobs.
- Sync model: near-real-time for operational dashboards, batch consolidation for financial close.
- Question I would ask first: which reports are currently delayed or manually adjusted during close?

## Scenario 4: Budgeting and Forecasting

**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

- Why important: better cash planning, staffing and capacity decisions, proactive variance management.
- Model inputs: seasonality, promotions, demand forecast, labor rates, transport rates, inflation.
- Design: rolling forecast, scenario planning (best/base/worst), variance analysis vs budget.
- Governance: monthly forecast cadence, ownership per cost center, threshold-based escalation.
- Question I would ask first: what forecast horizon is most useful for decisions here (weekly, monthly, quarterly)?

## Scenario 5: Cost Control in Warehouse Replacement

**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

- Preserve history to keep trend continuity, auditability, and budget accountability for the same business unit code.
- Replacement controls: carry over baseline KPIs, separate one-time migration costs vs run-rate costs.
- Budget protection: monitor ramp-up burn rate, temporary dual-running costs, transition risk reserve.
- Reporting: pre/post replacement comparative dashboards with normalized metrics.
- Question I would ask first: what is the acceptable budget variance during the transition period?

## Instructions for Candidates

Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
