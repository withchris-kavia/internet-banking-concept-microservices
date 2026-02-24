# Pilot Report (2026-02-24)

## 1. Overview

This report summarizes pilot activity and outputs captured in `project-metrics.json` for the repository described by the metrics file.

- Project root (from metrics): `/home/kavia/workspace/code-generation`
- Last updated timestamp: `2026-02-24T12:27:32.929336Z`
- Pilot duration window: Not Available. The metrics file includes `sessions_index.created_at` and `last_updated`, but only a single session is listed and there is no per-session end timestamp. To compute a reliable window, an expanded `sessions_index` with per-session end timestamps or a session log export would be needed.
- Total sessions: `60`
- Primary platform/framework observed: `backend` / `springboot` (based on `project_types.sessions_per_platform` and `project_types.sessions_per_framework`)

## 2. Delivery Outputs

| Metric | Value |
|---|---:|
| Documents generated (count) | 33 |
| Files edited (count) | 33 |
| LOC added | 5445 |
| LOC removed | 0 |
| LOC net | 5445 |
| Total cost (as given) | 10.06703375 |
| Sessions count | 60 |

## 3. Feature Utilization (Inspect / Plan / Build)

### Build Feature Usage

| Build feature | Count |
|---|---:|
| documentation | 59 |
| code_generation | 0 |
| bug_fixing | 0 |
| test_generation | 0 |
| test_execution | 0 |
| figma_extraction | 0 |
| image_extraction | 0 |
| research | 0 |
| website_extraction | 0 |
| code_analysis | 0 |
| document_analysis | 0 |
| query | 0 |

### Plan Feature Usage

| Plan feature | Count |
|---|---:|
| requirement_analysis | 0 |
| architecture_specs | 0 |
| design_specs | 0 |
| interface_specs | 0 |
| test_planning | 0 |
| document_creation | 0 |

### Inspect Feature Usage

| Inspect feature | Count |
|---|---:|
| code_analysis | 0 |
| document_generation | 0 |
| deep_analysis | 0 |

Top 3 most-used features by count are `documentation (59)`, and then a tie at `0` across all remaining features. Because ties exist, the “top 3” cannot be reduced to only three features without an arbitrary rule; all zero-count features are tied.

Features with zero usage are: all features except `build.documentation`. This indicates the recorded activity for this pilot is dominated by documentation-related work in the captured feature telemetry.

## 4. Cost and Agent Breakdown

### Agent cost table (absolute values)

| Agent | Cost |
|---|---:|
| PlanningAgent | 0.0 |
| CodeWritingAgent | 0.0 |
| TestCodeWritingAgent | 0.0 |
| TestExecutionAgent | 0.0 |
| BugFixingAndVerificationAgent | 0.0 |
| QuestionAnsweringAgent | 0.0 |
| DocumentationAgent | 135.9357218 |
| GeneralistAgent | 0.0 |
| DesignExtractionAgent | 0.0 |
| FigmaExtractionAgent | 0.0 |
| WebsiteExtractionAgent | 0.0 |
| others | 32.28004815 |
| KnowledgeDiff | 0.3133152 |

The highest cost agent is `DocumentationAgent (135.9357218)`, followed by `others (32.28004815)`. There is an anomaly between `totals.total_cost (10.06703375)` and the sum of `agent_costs`. The sum of `agent_costs` is `168.52908515`, which differs from `totals.total_cost` by `158.4620514` (agent_costs sum is higher). This report does not assume which value is authoritative. To resolve, the pilot needs either (a) a definition of what `totals.total_cost` represents (for example, a normalized or discounted cost), or (b) a recalculation export showing how totals are derived from agent costs.

## 5. Session Summary

The following table is reproduced from `sessions_index` in `project-metrics.json`.

| session_id | created_at | metrics_path |
|---|---|---|
| 7494a97e-5788-425b-afa5-9c802bec48db | 2026-02-24T12:17:32.230212Z | /home/kavia/workspace/code-generation/session-metrics.json |

Note: `sessions_count` is `60` but `sessions_index` contains only one entry in the available metrics file. The remaining session records are Not Available. To complete this section, the metrics export would need a full `sessions_index` list or the referenced per-session metrics files for all sessions.

## 6. Pilot Analytics Spreadsheet (SDLC Outcome Metrics)

### Pilot Analytics Spreadsheet: Not Found

No `.xlsx` “Pilot Analytics Dashboard” spreadsheet was found in the repository during search. To populate this section, an analytics file such as `Kavia_Pilot_Analytics_Dashboard.xlsx` (or similar) needs to be added to the repo (or provided to the agent).

Expected sheets (placeholders; all Not Available because the spreadsheet is missing):

#### Use Case Metrics (Not Available)

| Metric | Baseline | Kavia | Improvement | Notes |
|---|---:|---:|---:|---|
| Not Available | Not Available | Not Available | Not Available | Required source: Pilot analytics .xlsx sheet "Use Case Metrics" (or closest match). |

Key wins: Not Available. Required source: the "Use Case Metrics" sheet.
Gaps / needs more data: Not Available. Required source: the "Use Case Metrics" sheet.

#### Ingestion Metrics (Not Available)

| Metric | Baseline | Kavia | Improvement | Notes |
|---|---:|---:|---:|---|
| Not Available | Not Available | Not Available | Not Available | Required source: Pilot analytics .xlsx sheet "Ingestion Metrics" (or closest match). |

Key wins: Not Available. Required source: the "Ingestion Metrics" sheet.
Gaps / needs more data: Not Available. Required source: the "Ingestion Metrics" sheet.

#### Productivity Metrics (Not Available)

| Metric | Baseline | Kavia | Improvement | Notes |
|---|---:|---:|---:|---|
| Not Available | Not Available | Not Available | Not Available | Required source: Pilot analytics .xlsx sheet "Productivity Metrics" (or closest match). |

Key wins: Not Available. Required source: the "Productivity Metrics" sheet.
Gaps / needs more data: Not Available. Required source: the "Productivity Metrics" sheet.

#### Competitive Matrix (Not Available)

| Dimension | Baseline | Kavia | Improvement | Notes |
|---|---:|---:|---:|---|
| Not Available | Not Available | Not Available | Not Available | Required source: Pilot analytics .xlsx sheet "Competitive Matrix" (or closest match). |

Key wins: Not Available. Required source: the "Competitive Matrix" sheet.
Gaps / needs more data: Not Available. Required source: the "Competitive Matrix" sheet.

## 7. Competitive Positioning Summary

This section uses only `project-metrics.json` because the required “Competitive Matrix” spreadsheet is not available.

- The captured workflow coverage is heavily skewed toward documentation, with `build.documentation = 59` and all other tracked features at `0`, indicating the measured pilot activity focused on documentation outputs rather than code generation, testing, or bug fixing in the feature telemetry.
- The pilot produced measurable documentation output, with `totals.documents_generated = 33`.
- The pilot produced measurable codebase change output in aggregate, with `totals.files_edited = 33` and `totals.loc.net = 5445`.
- Platform/framework evidence in the metrics indicates a consistent stack across recorded sessions: `backend` platform and `springboot` framework (both `60` sessions).
- Toolchain integration readiness via MCP is Not Available because there is no explicit MCP configuration, code, or documentation in the repository evidence provided to this report, and the competitive matrix spreadsheet is missing.

## 8. Next Steps

### Checklist

- [ ] Add the pilot analytics spreadsheet to the repository (expected: `.xlsx` file, for example `Kavia_Pilot_Analytics_Dashboard.xlsx`) so Section 6 can be populated with SDLC outcome metrics and the competitive matrix.
- [ ] Export a complete `sessions_index` covering all `sessions_count = 60` sessions, including at minimum `session_id`, `created_at`, and a resolvable `metrics_path` for each session.
- [ ] Provide a definition or derivation for `totals.total_cost` and `agent_costs` so the discrepancy can be reconciled (for example, whether totals are normalized, discounted, or computed from a different cost basis).

### Recommendations to increase measurable outcomes

Instrument the pilot outputs so that baseline-versus-Kavia comparisons can be computed without ambiguity. This includes storing the analytics spreadsheet, exporting complete session indexes, and ensuring the cost model is consistent between totals and per-agent breakdown.

### Suggested targets for next period

- Completion rate target: TBD (requires baseline use-case list and spreadsheet metrics).
- Ingestion time target: TBD (requires ingestion metrics in spreadsheet).
- Productivity improvement target: TBD (requires productivity metrics in spreadsheet).

Final path: `internet-banking-concept-microservices/kavia-docs/Pilot Report (2026-02-24).md`

Pilot report generated.
