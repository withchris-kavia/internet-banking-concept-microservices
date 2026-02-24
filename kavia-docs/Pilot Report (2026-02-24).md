# Kavia AI Pilot Report with Competitive Metrics — 2026-02-24

## 1. Executive Summary

### Objectives

This pilot report documents measurable outcomes from using Kavia on the `internet-banking-concept-microservices` repository, with an emphasis on SDLC telemetry, documentation output, and evidence-based competitive positioning. The report follows a strict “do not fabricate” policy: where a baseline and/or “with Kavia” value cannot be computed from repository evidence or captured run history, the metric is marked Not Available with concrete measurement guidance.

### What was executed (use-cases)

The repository contains explicit evidence of Kavia-assisted execution for a discrete use-case: implementing and documenting an “Internal Transfer API” within the Fund Transfer microservice, including requirement traceability documentation.

The observable use-case executed in-repo is:

1. Internal Transfer API feature delivery for the Fund Transfer microservice, including endpoint implementation, documentation, and traceability artifacts.

### Key measurable outcomes (top 5)

1. The repository contains a complete “Internal Transfer API – Completion Report” that explicitly states the feature was implemented by a “Kavia Code Generation Agent,” providing direct evidence that Kavia was used for feature delivery.
2. Documentation artifacts for the Internal Transfer feature were added under `kavia-docs/` and `kavia-docs/CodeWiki/Specs/FeatureSpecs/`, including a requirement traceability matrix, providing measurable “after pilot” documentation output.
3. The CodeWiki Feature Specs index references the new traceability document, demonstrating discoverability integration into an existing documentation navigation surface.
4. The feature is documented as backward compatible and “ready for deployment,” with deployment and verification steps described in the completion report, which indicates operational readiness guidance was produced.
5. The repository includes Docker Compose definitions for the full microservices environment (Zipkin, Keycloak, PostgreSQL, MySQL, and the microservices), providing clear evidence of runtime integration points, even though pilot-time MCP telemetry is not captured.

### Risks/limitations of measurement

This repository does not provide an explicit pilot boundary marker (no `kavia-pilot.json`, no `pilot-start` tag/branch visible from the inspected files, and no Kavia run IDs/logs in-repo). As a result, “Baseline vs With Kavia” metrics cannot be computed for most time-based or process-based measures (cycle time, planning reduction, adoption, ingestion time). Additionally, there is no CI telemetry, coverage reports, or PR/issue metadata stored in-repo that would allow quantitative SDLC comparisons. The report therefore treats most metrics as Not Available and provides concrete instrumentation steps to enable measurement in a future iteration.

## 2. Pilot Team

Customer Team:

Development Lead:

Developers:

Product Manager (PM):

Kavia Team:

Pilot Lead:

Solutions Architect:

Customer Success Manager:

## 3. Use-Case Completion Metrics

| Metric | Baseline | With Kavia | Improvement | Evidence/Notes |
|---|---:|---:|---:|---|
| Use-Case Completion Rate (%) | Not Available | Not Available | Not Available | Not Available. Why missing: the repository does not define a list of planned pilot use-cases nor a pilot window boundary; only evidence of one completed feature exists. How to measure next: define planned pilot use-cases in a marker file (for example `kavia-docs/pilot-scope.md`) and record completion status with links to commits/PRs and/or Kavia run IDs; compute completed/planned at end of pilot. Measurement Notes: Data sources inspected include `INTERNAL_TRANSFER_COMPLETION_REPORT.md` and `CHANGELOG-internal-transfer.md`; method was “detect explicit completion statements,” which is insufficient to compute a completion rate because “planned” is unknown. |
| End-to-End Workflow Coverage | Not Available | Not Available | Not Available | Not Available. Why missing: no workflow inventory and no automated end-to-end tests or run logs are included in the repository. How to measure next: define the target workflow list (for example authentication, account lookup, internal transfer, external transfer, utility payment) and capture automated E2E execution via Postman/Newman or an API test runner in CI with a published pass/fail report. Measurement Notes: The repository contains Postman collection references in `README.md`, but no stored execution output was found. |
| Implementation Plan Quality Score (1-5) | Not Available | Estimated: 4 | Not Available | Estimated. Why baseline missing: no pre-pilot implementation plan artifacts exist in-repo for comparison. With Kavia estimate is based on the completeness of the written plan-like artifacts included in the completion report (deployment checklist, verification steps, monitoring recommendations, risk/limitations). How to measure next: store plan documents per feature (for example in `kavia-docs/Plans/`) and apply a fixed rubric to both baseline and Kavia-assisted plans. Measurement Notes: Data sources are `INTERNAL_TRANSFER_COMPLETION_REPORT.md` and `kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md`. Rubric used: (1) clarity of scope and deliverables, (2) explicit acceptance/verification steps, (3) testability guidance, (4) risk handling, (5) dependency/integration mapping; scoring was 4/5 because the artifacts cover deliverables, verification, operations, and architecture mapping but do not include automated test evidence or quantified SLOs. |
| Test Case Generation Coverage (%) | Not Available | Not Available | Not Available | Not Available. Why missing: there is no evidence of Kavia-generated test cases for the Internal Transfer feature (existing tests are present in the fund transfer service, but there is no baseline/with-Kavia mapping, and the completion report explicitly describes manual verification as current practice). How to measure next: require that new features include unit/integration tests and tag tests with a feature ID; compute coverage as “features with automated tests / features delivered” or “planned test cases generated / planned test cases” based on a stored test plan. Measurement Notes: Source indicates manual verification emphasis in `kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md` under Verification Artifacts and in `INTERNAL_TRANSFER_COMPLETION_REPORT.md`. |

## 4. Codebase Ingestion and Knowledge Graph Creation

| Metric | Baseline | With Kavia | Improvement | Evidence/Notes |
|---|---:|---:|---:|---|
| Time to Codebase Comprehension (hours) | Not Available | Not Available | Not Available | Not Available. Why missing: no Kavia ingestion/comprehension timestamps or logs are stored in this repo. How to measure next: record (a) ingestion start/end timestamps, (b) first “accurate architecture summary” timestamp, and (c) first successful multi-file change timestamp in Kavia run logs; export these per run and aggregate. Measurement Notes: No Kavia run IDs or run logs were found in the inspected repository files. |
| Repository Coverage (%) | Not Available | Not Available | Not Available | Not Available. Why missing: there is no index manifest showing what files were parsed/indexed by Kavia. How to measure next: emit an ingestion manifest (for example `kavia-ingestion.json`) listing total candidate files, excluded patterns, parsed files by language, and parse success rate; compute parsed/total. Measurement Notes: Repository structure and languages were inferred from module layout and Gradle build files, but that is insufficient to compute an ingestion percentage. |
| Cross-File Reasoning Accuracy (%) | Not Available | Not Available | Not Available | Not Available. Why missing: there is no ground truth QA/eval dataset or recorded Q&A accuracy metrics in the repository. How to measure next: create a small evaluation harness with questions whose answers are deterministically verifiable against code (for example, endpoint paths, DTO fields, database tables used) and score Kavia answers; store results per run. Measurement Notes: The repository includes cross-service integration (Feign, Gateway), which would support an eval set, but none is present. |
| Dependency Mapping Completeness (%) | Not Available | Not Available | Not Available | Not Available. Why missing: no stored dependency graph export from Kavia and no baseline dependency graph snapshot. How to measure next: generate a dependency graph from Gradle and runtime configuration (services, ports, routes) and compare to Kavia’s inferred graph; score completeness as “edges found / edges expected.” Measurement Notes: Evidence for dependencies exists in `docker-compose/docker-compose.yml` and service `build.gradle` files (Eureka, Gateway, OpenFeign, Zipkin tracing), but no computed graph artifact is present. |

## 5. Documentation and CodeWiki Impact

| Metric | Before Pilot | After Pilot | Impact | Evidence/Notes |
|---|---:|---:|---:|---|
| Documentation Coverage (%) | Not Available | Not Available | Not Available | Not Available. Why missing: there is no explicit module-to-document mapping or a pre-pilot snapshot of documentation coverage, and the repository has no pilot boundary marker to determine “before” content reliably. How to measure next: define the denominator explicitly (for example “microservices/modules requiring a doc page”) and store a documentation inventory file that maps each module to its doc(s); compute documented/total at baseline and after pilot. Measurement Notes: While docs exist (`README.md`, `kavia-docs/*`), the repository lacks a baseline marker and a defined coverage denominator. |
| Architecture Artifacts Generated (#) | Not Available | 1 | Not Available | With Kavia = 1 (observed). Evidence: a CodeWiki “traceability matrix” artifact exists at `kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md`, which is an architecture/requirements artifact tied to implementation. Baseline is Not Available because “before pilot” window is not determinable from repo evidence. How to measure next: add a pilot start tag/marker file and count new architecture artifacts (LLDs, ADRs, diagrams, traceability docs) added after that marker. Measurement Notes: Count method: enumerate new artifact files explicitly listed as “New Files” in `CHANGELOG-internal-transfer.md` and confirm presence in the repository. |
| Sequence Diagrams Generated (#) | Not Available | Not Available | Not Available | Not Available. Why missing: there are no Mermaid/PlantUML sequence diagrams stored as sequence diagrams in the inspected paths. How to measure next: standardize diagram storage under `kavia-docs/CodeWiki/Architecture/` (or similar) and count sequence-diagram files by convention (for example filenames containing `sequence` or Mermaid `sequenceDiagram` blocks). Measurement Notes: The completion report includes an ASCII flow diagram, but it is not a formal sequence diagram artifact. |
| Onboarding Time Reduction (%) | Not Available | Not Available | Not Available | Not Available. Why missing: no onboarding time telemetry exists (for example time-to-first-task for new developers) and no baseline measurements are captured. How to measure next: run an onboarding study with a checklist and time tracking; compare baseline onboarding vs onboarding using CodeWiki and Kavia-produced docs. Measurement Notes: The repo has onboarding-oriented information in `README.md` and Kavia-produced internal transfer documentation, but no time measurements. |

## 6. Developer Productivity Metrics

| Metric | Baseline | With Kavia | Improvement | Evidence/Notes |
|---|---:|---:|---:|---|
| Issue-to-PR Cycle Time (days) | Not Available | Not Available | Not Available | Not Available. Why missing: the repository does not include issue/PR metadata exports, and commit messages inspected do not provide PR references in the accessible evidence set. How to measure next: integrate with GitHub/GitLab API exports for PR open/merge timestamps and issue created/closed timestamps; compute per issue/PR and report percentiles. Measurement Notes: No `.github/` telemetry or PR metadata files were found in the inspected set, and no git history was available via the provided evidence. |
| Planning Time Reduction (%) | Not Available | Not Available | Not Available | Not Available. Why missing: no time tracking for planning activities is stored in-repo. How to measure next: record planning session durations and artifact production time (for example time from task start to approved plan) with and without Kavia; store in a pilot telemetry log. Measurement Notes: The completion report suggests structured planning outputs exist, but time measurements are absent. |
| Refactor Confidence Score (1-5) | Not Available | Not Available | Not Available | Not Available. Why missing: no surveys, code review confidence signals, or automated regression/coverage data is stored. How to measure next: define a lightweight post-change survey plus objective proxies (test pass rate, coverage delta, defect rate); measure per refactor. Measurement Notes: Existing tests exist in some services, but there is no mapping of refactors to those tests, and no run telemetry. |
| Weekly Developer AI Adoption (%) | Not Available | Not Available | Not Available | Not Available. Why missing: no user-level adoption telemetry (who used AI tools when) is stored in this repo. How to measure next: use IDE plugin telemetry, Kavia run user attribution, or survey-based reporting aggregated weekly. Measurement Notes: The only user attribution found is “Implemented By: Kavia Code Generation Agent” in `INTERNAL_TRANSFER_COMPLETION_REPORT.md`, which does not support a weekly adoption metric. |

## 7. Skills Framework Metrics

| Metric | Baseline | With Kavia | Improvement | Evidence/Notes |
|---|---:|---:|---:|---|
| Reusable AI Skills Created (#) | Not Available | Not Available | Not Available | Not Available. Why missing: there is no “skills library” or prompts/playbooks folder in the inspected repository paths, and no structured reusable skill artifacts are clearly defined. How to measure next: create a `kavia-skills/` directory (or similar) with versioned skill definitions and count skills added during the pilot window. Measurement Notes: Existing Kavia outputs are feature documentation artifacts under `kavia-docs/`, not reusable skill definitions. |
| Standards Conformance (%) | Not Available | Not Available | Not Available | Not Available. Why missing: no formal linting/formatting/checkstyle configuration or quality gate outputs were examined that define a measurable standard conformance metric, and there is no CI report artifact. How to measure next: add standard checks (Spotless/Checkstyle/PMD/SpotBugs) and publish pass/fail and violation counts; compute conformance as “checks passed / checks run” or “violations per KLOC” tracked over time. Measurement Notes: Gradle builds show standard Spring dependencies but no explicit lint gate configuration in the inspected build files. |
| Cross-Team Skill Adoption (%) | Not Available | Not Available | Not Available | Not Available. Why missing: no cross-team usage telemetry exists and no skill artifacts are present. How to measure next: track skill invocation in Kavia runs and map to teams/projects; compute adoption over a fixed time window. Measurement Notes: Not inferable from repository content. |
| Consistency Score (1-5) | Not Available | Not Available | Not Available | Not Available. Why missing: no defined rubric or multi-feature sample exists to score consistency of outputs across teams/features. How to measure next: define a rubric (structure adherence, terminology consistency, traceability completeness) and score multiple Kavia-produced artifacts over time. Measurement Notes: Only one clear feature artifact set (Internal Transfer) is evidenced. |

## 8. MCP Integration Metrics

| Metric | Baseline | With Kavia | Improvement | Evidence/Notes |
|---|---:|---:|---:|---|
| Toolchain Integrations Connected (#) | Not Available | Not Available | Not Available | Not Available. Why missing: MCP connection telemetry (what tools were connected through MCP) is not stored in the repository. How to measure next: log MCP tool connections per run (for example SCM, issue tracker, CI, observability, doc store) and store a run summary artifact. Measurement Notes: While the runtime stack includes Zipkin/Keycloak/MySQL/Postgres per Docker Compose, this is not evidence of MCP connections. |
| Context Switching Reduction (%) | Not Available | Not Available | Not Available | Not Available. Why missing: no baseline measurement of developer context switching and no Kavia session telemetry in-repo. How to measure next: instrument IDE/app switching telemetry (or self-reported time logs) during comparable tasks; compute reduction with Kavia. Measurement Notes: Not measurable from repository artifacts alone. |
| Workflow Automation Coverage (%) | Not Available | Not Available | Not Available | Not Available. Why missing: no defined SDLC step inventory and no automation telemetry. How to measure next: define SDLC steps in scope (for example plan, implement, test, document, release) and record which were automated by Kavia; compute automated/total. Measurement Notes: The internal transfer feature shows automated documentation output, but the step inventory is not defined, so a percentage cannot be computed. |
| Auditability and Traceability Score (1-5) | Not Available | Estimated: 4 | Not Available | Estimated. Why baseline missing: no pre-pilot traceability artifacts are evidenced in the inspected set for comparison. With Kavia estimate is based on the presence of a detailed requirement traceability matrix mapping requirements to code and verification guidance. How to measure next: define a traceability scoring rubric and apply to baseline artifacts and Kavia artifacts across multiple features. Measurement Notes: Evidence comes from `kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md` and the explicit deliverables checklist in `INTERNAL_TRANSFER_COMPLETION_REPORT.md`. Heuristic: score 4/5 because traceability is explicit and maintained, but verification is primarily manual and there is no automated CI evidence included. |

## 9. Competitive Differentiation Matrix

| Capability | Kavia | Copilot | Cursor | Amazon Q | Sourcegraph | Evidence/Notes |
|---|---|---|---|---|---|---|
| End-to-End Use Case Execution | Evidence of executing a feature end-to-end including code + docs | Unknown/Varies | Unknown/Varies | Unknown/Varies | Unknown/Varies | Kavia evidence: `INTERNAL_TRANSFER_COMPLETION_REPORT.md` states “Implemented By: Kavia Code Generation Agent” and documents deliverables across implementation and documentation. Competitors: no verifiable, pilot-specific sources were provided in-repo; therefore marked Unknown/Varies. |
| Codebase Ingestion Depth | Not Available | Unknown/Varies | Unknown/Varies | Unknown/Varies | Unknown/Varies | Not Available for Kavia because no ingestion manifests or run logs were found. Competitors remain Unknown/Varies absent cited sources. |
| Structured Documentation Output | Evidence of structured, multi-document output including CodeWiki traceability | Unknown/Varies | Unknown/Varies | Unknown/Varies | Unknown/Varies | Kavia evidence: new docs listed in `CHANGELOG-internal-transfer.md` under “Documentation Added” and the CodeWiki traceability file at `kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md`. |
| Reusable Skills Framework | Not Available | Unknown/Varies | Unknown/Varies | Unknown/Varies | Unknown/Varies | No skill library artifacts found in this repository for the pilot. Competitors not assessed without a verifiable source. |
| MCP Toolchain Integration | Not Available | Unknown/Varies | Unknown/Varies | Unknown/Varies | Unknown/Varies | No MCP connection/run telemetry was found in-repo; therefore Not Available for Kavia in this pilot context. |
| Cross-Repo SDLC Intelligence | Not Available | Unknown/Varies | Unknown/Varies | Unknown/Varies | Unknown/Varies | This repo does not include cross-repo signals or Kavia SDLC intelligence exports; therefore Not Available for Kavia. |

## 10. Recommendations & Next Instrumentation Steps

Most metrics in this report are Not Available because the repository does not contain (a) an explicit pilot baseline boundary and (b) stored telemetry artifacts (Kavia run logs/IDs, CI timings, coverage reports, issue/PR exports, ingestion manifests). The primary recommendation is to treat telemetry as a first-class pilot artifact and store it in-repo (or store links/IDs that are resolvable).

### Instrumentation Checklist

- Add a pilot boundary marker file, such as `kavia-docs/pilot-start.md`, that contains the baseline timestamp, scope, and planned use-cases.
- Create a `kavia-docs/pilot-scope.md` listing planned use-cases with IDs, acceptance criteria, and a completion field that links to commits/PRs and Kavia run IDs.
- Store Kavia run summaries per execution (for example `kavia-docs/pilot-runs/<run-id>.md`) including start/end timestamps, agent tasks performed, and tool connections.
- Add CI that publishes:
  - test results (JUnit XML)
  - build duration
  - code coverage (JaCoCo) for each service where applicable
- Export issue/PR metadata (created/opened/merged timestamps, labels) or integrate an automated report generator in CI.
- Add an ingestion manifest export from Kavia (or wrapper script) that records indexed files, excluded patterns, parse success rate, and time spent.
- Add an evaluation harness for cross-file reasoning accuracy with a fixed question set and deterministic answers, storing scores per run.
- Define a documentation coverage denominator (services/modules/pages) and store a docs inventory mapping each module to its docs.

## Appendix: Evidence & Queries

### Files inspected

- `README.md`
- `CHANGELOG-internal-transfer.md`
- `INTERNAL_TRANSFER_COMPLETION_REPORT.md`
- `kavia-docs/CodeWiki/Specs/FeatureSpecs/index.md`
- `kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md`
- `docker-compose/docker-compose.yml`
- `docker-compose/docker-compose-support-apps.yml`
- Service build files:
  - `core-banking-service/build.gradle`
  - `internet-banking-fund-transfer-service/build.gradle`
  - `internet-banking-user-service/build.gradle`
  - `internet-banking-utility-payment-service/build.gradle`
  - `internet-banking-api-gateway/build.gradle`
  - `internet-banking-config-server/build.gradle`
  - `internet-banking-service-registry/build.gradle`

### Commands (if any)

No shell commands were run to compute metrics in this report. All populated values were derived from direct repository file inspection.

### Tags/commit ranges

Not Available. No git tags/commit ranges were available from the inspected evidence set. To enable this section, export git history (for example using `git log --since/--until`) and store pilot boundary tags (for example `pilot-start` and `pilot-end`).

### Kavia run IDs/log references

Not Available. No Kavia run IDs or run logs were found stored in-repository.

### Parsing rules used

- Documentation artifact counting in Section 5 was based on explicit “New Files” enumeration in `CHANGELOG-internal-transfer.md` and confirmation that at least one of those artifacts exists in the repo under CodeWiki (`kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md`).
- Kavia usage evidence was based on an explicit attribution line “Implemented By: Kavia Code Generation Agent” in `INTERNAL_TRANSFER_COMPLETION_REPORT.md`.
