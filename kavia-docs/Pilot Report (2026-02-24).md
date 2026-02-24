# Pilot Report (2026-02-24)

## 1. Overview

This report summarizes pilot activity and outputs captured in `project-metrics.json`, and includes SDLC outcome metrics reproduced from the attached “Pilot Analytics Dashboard” spreadsheet.

- Project root (from metrics): `/home/kavia/workspace/code-generation`
- Last updated timestamp: `2026-02-24T12:32:53.270040Z`
- Pilot duration window: Not Available. The metrics file includes `sessions_index.created_at` and `last_updated`, but `sessions_index` contains only a single session record and there is no per-session end timestamp. To compute a reliable window, an expanded `sessions_index` with all sessions and per-session end timestamps (or a session log export) would be needed.
- Total sessions: `92`
- Primary platform/framework observed: `backend` / `springboot` (based on `project_types.sessions_per_platform` and `project_types.sessions_per_framework`)

## 2. Delivery Outputs

| Metric | Value |
|---|---:|
| Documents generated (count) | 65 |
| Files edited (count) | 93 |
| LOC added | 14309 |
| LOC removed | 3360 |
| LOC net | 10949 |
| Total cost (as given) | 26.6468586 |
| Sessions count | 92 |

## 3. Feature Utilization (Inspect / Plan / Build)

### Build Feature Usage

| Build feature | Count |
|---|---:|
| documentation | 167 |
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

Top 3 most-used features by count are `documentation (167)`, and then a tie at `0` across all remaining features. Because ties exist, the “top 3” cannot be reduced to only three features without an arbitrary rule; all zero-count features are tied.

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
| DocumentationAgent | 615.3851416 |
| GeneralistAgent | 0.0 |
| DesignExtractionAgent | 0.0 |
| FigmaExtractionAgent | 0.0 |
| WebsiteExtractionAgent | 0.0 |
| others | 127.01690785 |
| KnowledgeDiff | 1.3571682 |

The highest cost agent is `DocumentationAgent (615.3851416)`, followed by `others (127.01690785)`. There is an anomaly between `totals.total_cost (26.6468586)` and the sum of `agent_costs`. The sum of `agent_costs` is `743.75921765`, which differs from `totals.total_cost` by `717.11235905` (agent_costs sum is higher). This report does not assume which value is authoritative. To resolve, the pilot needs either (a) a definition of what `totals.total_cost` represents (for example, a normalized or discounted cost), or (b) a recalculation export showing how totals are derived from agent costs.

## 5. Session Summary

The following table is reproduced from `sessions_index` in `project-metrics.json`.

| session_id | created_at | metrics_path |
|---|---|---|
| 7494a97e-5788-425b-afa5-9c802bec48db | 2026-02-24T12:17:32.230212Z | /home/kavia/workspace/code-generation/session-metrics.json |

Note: `sessions_count` is `92` but `sessions_index` contains only one entry in the available metrics file. The remaining session records are Not Available. To complete this section, the metrics export would need a full `sessions_index` list or the referenced per-session metrics files for all sessions.

## 6. Pilot Analytics Spreadsheet (SDLC Outcome Metrics)

The following tables are reproduced from the attached “Pilot Analytics Dashboard” spreadsheet. Per the instructions, “Improvement” is computed where possible using these rules: time metrics use `baseline - with_kavia` (positive is faster), and score/percentage metrics use `with_kavia - baseline` (positive is better). Where the spreadsheet provides only `Baseline` and `With Kavia`, “Improvement” is computed directly from those values.

### Use Case Metrics

| Metric | Baseline | With Kavia | Improvement | Notes |
|---|---:|---:|---:|---|
| Use-Case Completion Rate (%) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |
| End-to-End Workflow Coverage (%) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |
| Implementation Plan Quality (1-5) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |
| Test Case Generation Coverage (%) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |

Key wins: The spreadsheet values are all `0`, so no quantitative wins can be identified from this sheet without additional/updated baseline and pilot measurements.

Gaps / needs more data: The sheet appears to be a template or placeholder export because all fields are `0`. To quantify outcomes, the pilot needs measured baseline values and measured “With Kavia” values for each metric.

### Ingestion Metrics

| Metric | Baseline | With Kavia | Improvement | Notes |
|---|---:|---:|---:|---|
| Time to Codebase Comprehension (hrs) | 0 | 0 | 0 | Improvement computed as baseline - with_kavia (time metric). |
| Repository Coverage (%) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |
| Cross-File Reasoning Accuracy (%) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |
| Dependency Mapping Completeness (%) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |

Key wins: The spreadsheet values are all `0`, so no quantitative wins can be identified from this sheet without additional/updated baseline and pilot measurements.

Gaps / needs more data: The sheet appears to contain placeholder values. To compute meaningful improvements, baseline and “With Kavia” should be measured for each metric (for example, via time tracking and evaluation rubrics).

### Productivity Metrics

| Metric | Baseline | With Kavia | Improvement | Notes |
|---|---:|---:|---:|---|
| Issue-to-PR Cycle Time (days) | 0 | 0 | 0 | Improvement computed as baseline - with_kavia (time metric). |
| Planning Time Reduction (%) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline (percent metric as provided). |
| Refactor Confidence (1-5) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |
| AI Adoption Rate (%) | 0 | 0 | 0 | Improvement computed as with_kavia - baseline. |

Key wins: The spreadsheet values are all `0`, so no quantitative wins can be identified from this sheet without additional/updated baseline and pilot measurements.

Gaps / needs more data: The sheet appears to contain placeholder values. To quantify productivity outcomes, the pilot needs measured cycle times, measured planning time (or a defined reduction calculation method), and adoption tracking.

### Competitive Matrix

This table is reproduced from the spreadsheet. Because this matrix compares multiple tools and does not include a “Baseline” column, no “Improvement” is computed in this table.

| Capability | Kavia | Copilot | Cursor | Amazon Q | Sourcegraph |
|---|---:|---:|---:|---:|---:|
| End-to-End Use Case Execution | 0 | 0 | 0 | 0 | 0 |
| Codebase Ingestion Depth | 0 | 0 | 0 | 0 | 0 |
| Structured Documentation Output | 0 | 0 | 0 | 0 | 0 |
| Reusable Skills Framework | 0 | 0 | 0 | 0 | 0 |
| MCP Toolchain Integration | 0 | 0 | 0 | 0 | 0 |
| Cross-Repo SDLC Intelligence | 0 | 0 | 0 | 0 | 0 |

Key wins: The spreadsheet values are all `0`, so no competitive advantages can be supported quantitatively from this sheet alone.

Gaps / needs more data: The matrix appears to be unscored (all `0`). To support competitive positioning, the pilot needs a defined scoring rubric plus completed scores per capability.

## 7. Competitive Positioning Summary

Using the available spreadsheet (Competitive Matrix is unscored) and `project-metrics.json`, the following differentiation points are supported by the repository metrics (not by the spreadsheet scoring):

- End-to-end workflow coverage in feature telemetry is limited to documentation tasks: `build.documentation = 167` while all other tracked features are `0`, indicating measured pilot activity focused on documentation outputs rather than code generation, testing, or bug fixing.
- Documentation generation output is measurable: `totals.documents_generated = 65` and `build.documentation = 167`.
- Codebase changes output is measurable in aggregate: `totals.files_edited = 93`, with `totals.loc.added = 14309`, `totals.loc.removed = 3360`, and `totals.loc.net = 10949`.
- Primary platform/framework usage is consistent across sessions (`backend` / `springboot`, both `92` sessions).
- Toolchain integration readiness via MCP is Not Available because there is no explicit MCP configuration, code, or documentation in the repository evidence used for this report, and the spreadsheet matrix includes MCP as a row but provides no scored evidence.

## 8. Next Steps

### Checklist

- [ ] Provide a complete `sessions_index` covering all `sessions_count = 92` sessions, including at minimum `session_id`, `created_at`, and a resolvable `metrics_path` for each session (or add all referenced per-session metrics files).
- [ ] Reconcile cost reporting by providing a definition/derivation for `totals.total_cost` versus `agent_costs`, since the current values differ by `717.11235905`.
- [ ] Update the Pilot Analytics Dashboard spreadsheet with actual measured baseline and “With Kavia” values (the attached sheet appears to contain placeholder zeros), and document the scoring rubric used for the Competitive Matrix.

### Recommendations to increase measurable outcomes

Instrument the pilot so baseline-versus-Kavia comparisons can be computed without ambiguity. This includes tracking time-based metrics (e.g., comprehension time, issue-to-PR cycle time), defining scoring rubrics for qualitative metrics (e.g., plan quality, refactor confidence), and ensuring competitive matrix scores are populated consistently.

### Suggested targets for next period

- Completion rate target: TBD (requires non-zero baseline and measured “With Kavia” completion rates).
- Ingestion time target: TBD (requires measured comprehension times).
- Productivity improvement target: TBD (requires measured cycle times and a defined reduction calculation method).
- Competitive matrix scoring completion: TBD (requires a rubric and filled scores per capability).

Final path: `internet-banking-concept-microservices/kavia-docs/Pilot Report (2026-02-24).md`

Pilot report generated.
