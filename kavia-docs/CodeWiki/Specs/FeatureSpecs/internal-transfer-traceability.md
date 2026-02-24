---
id: internal-transfer-traceability
type: traceability
title: "Internal Transfer API - Requirement Traceability Matrix"
description: "Requirement-to-code traceability for the internal transfer feature implementation"
tags:
  - traceability
  - requirements
  - internal-transfer
  - fund-transfer
---

[CodeWiki](../../index.md) / [Specs](../index.md) / [Feature Specs](index.md)

# Internal Transfer API - Requirement Traceability Matrix

## Purpose

This document provides explicit requirement-to-code traceability for the internal transfer API feature added to the Fund Transfer microservice. It maps requirements from the work item and PRD to concrete implementation units, inline requirement markers, and verification artifacts.

## Requirement Inventory

### REQ-IT-001: Internal Transfer REST API Endpoint
- **Type**: Functional (Derived from FR-006)
- **Source**: Work item subtask: "Generate a new API for the Fund Transfer Microservice that will complete internal transfers between existing accounts"
- **Statement**: The Fund Transfer microservice shall provide a dedicated REST API endpoint at `/api/v1/transfer/internal` that accepts internal transfer requests and returns transfer responses with transaction details.

### REQ-IT-002: Internal Transfer Request Model
- **Type**: Functional (Derived from FR-006)
- **Source**: Work item subtask: "Implement a new internal transfer REST API in the Fund Transfer microservice, integrating with existing accounts/balances/transactions logic"
- **Statement**: The internal transfer API shall accept requests containing fromAccount, toAccount, amount, authID, and optional description fields.

### REQ-IT-003: Internal Transfer Response Model
- **Type**: Functional (Derived from FR-006)
- **Source**: Work item action: "Update code and any related configuration/docs as needed"
- **Statement**: The internal transfer API shall return responses containing message, transactionId, fromAccount, toAccount, amount, and status fields.

### REQ-IT-004: Transfer Orchestration and Persistence
- **Type**: Functional (Derived from FR-006)
- **Source**: PRD FR-006: "Fund transfers can be initiated and persisted, calling core banking for authoritative balance updates"
- **Statement**: The internal transfer implementation shall persist a PENDING transfer record locally before invoking core banking, then update the record to SUCCESS with the transaction reference upon successful completion, or FAILED on error.

### REQ-IT-005: Core Banking Integration
- **Type**: Functional (Derived from FR-006 and LLD)
- **Source**: LLD Section 6: "Core banking transaction processing (fund transfer...)"
- **Statement**: The internal transfer implementation shall convert internal transfer requests to core banking fund transfer format and delegate to the existing core banking transaction endpoint for authoritative balance validation and updates.

### REQ-IT-006: Error Handling and Status Tracking
- **Type**: Functional (Derived from NFR-003)
- **Source**: PRD NFR-003: "Data consistency within core banking operations"
- **Statement**: The internal transfer implementation shall catch exceptions from core banking calls, mark the local transfer record as FAILED, and propagate the error with context to the caller.

### REQ-IT-007: OpenAPI Documentation
- **Type**: Non-functional
- **Source**: Work item action: "Update code and any related configuration/docs as needed"
- **Statement**: The internal transfer API shall include OpenAPI/Swagger annotations with operation summaries, descriptions, and schema documentation.

## Requirement Trace Matrix

| Req ID | Requirement | Source | Implementation Mapping | Inline Code Trace | Verification Mapping | Status | Notes |
|---|---|---|---|---|---|---|---|
| REQ-IT-001 | Internal Transfer REST API Endpoint | Work item subtask | `FundTransferController.java`: `internalTransfer(@RequestBody InternalTransferRequest)` | `FundTransferController.java:internalTransfer` with REQ: FR-006 marker | Manual verification via API test (described below) | Implemented | Endpoint path `/api/v1/transfer/internal` with POST method |
| REQ-IT-002 | Internal Transfer Request Model | Work item subtask | `InternalTransferRequest.java`: fields `fromAccount`, `toAccount`, `amount`, `authID`, `description` | `InternalTransferRequest.java:class` with REQ: FR-006 marker | Schema validation via OpenAPI spec | Implemented | Includes Swagger schema annotations |
| REQ-IT-003 | Internal Transfer Response Model | Work item action | `InternalTransferResponse.java`: fields `message`, `transactionId`, `fromAccount`, `toAccount`, `amount`, `status` | `InternalTransferResponse.java:class` with REQ: FR-006 marker | Response contract verified by return type and builder usage | Implemented | Uses Lombok @Builder for construction |
| REQ-IT-004 | Transfer Orchestration and Persistence | PRD FR-006 | `FundTransferService.java`: `internalTransfer(InternalTransferRequest)` method | `FundTransferService.java:internalTransfer` with multiple REQ: FR-006 markers at key enforcement points | Manual verification via database inspection after API call | Implemented | Persists PENDING, calls core, updates to SUCCESS/FAILED |
| REQ-IT-005 | Core Banking Integration | LLD Section 6 | `FundTransferService.java`: `internalTransfer` method conversion logic to `FundTransferRequest` and `bankingCoreFeignClient.fundTransfer(coreRequest)` call | `FundTransferService.java:internalTransfer` (conversion and delegation block) with REQ: FR-006 marker | Manual verification via log inspection showing core banking call | Implemented | Reuses existing Feign client and core banking endpoint |
| REQ-IT-006 | Error Handling and Status Tracking | PRD NFR-003 | `FundTransferService.java`: try-catch block in `internalTransfer`, setting `TransactionStatus.FAILED` on exception | `FundTransferService.java:internalTransfer` catch block with REQ: FR-006 marker | Manual verification via insufficient funds test scenario | Implemented | Exception logged and propagated with context |
| REQ-IT-007 | OpenAPI Documentation | Work item action | `@Operation` annotations in `FundTransferController.internalTransfer` and `@Schema` annotations in request/response DTOs | `FundTransferController.java:internalTransfer` and DTO classes | OpenAPI spec inspection at `/v3/api-docs` endpoint | Implemented | Includes operation summary, description, and schema details |

## Inline Requirement ID Convention

All inline requirement markers use the format:
```java
// REQ: <REQ-ID> - <short hint>
```

For this implementation, all inline markers reference the parent requirement **FR-006** from the PRD ("Fund transfers can be initiated and persisted, calling core banking for authoritative balance updates") because all derived requirements trace back to that functional requirement.

### Placement Rules Applied

1. **Controller entrypoint**: `FundTransferController.internalTransfer` method has REQ: FR-006 in its Javadoc.
2. **Service flow orchestration**: `FundTransferService.internalTransfer` method has REQ: FR-006 in its method-level Javadoc and at each key step:
   - Persistence of PENDING record
   - Conversion to core banking format
   - Delegation to core banking
   - Update to SUCCESS status
   - Error handling and FAILED status update
3. **Request/Response DTOs**: Class-level Javadoc includes REQ: FR-006 to indicate these models fulfill the transfer data contract requirement.
4. **Enum update**: `TransactionStatus` enum updated with REQ: FR-006 marker to indicate FAILED status is required for transfer error tracking.

## Verification Artifacts

### Manual Verification Procedure

Since this repository does not contain automated tests for the fund transfer service (test directory exists but contains only a placeholder application test), verification is performed manually:

#### Verification Test 1: Successful Internal Transfer
1. Start the Docker Compose environment per repository README.
2. Authenticate and obtain a valid JWT token via Keycloak.
3. Send POST request to `http://localhost:8082/api/v1/transfer/internal` via API Gateway with:
   ```json
   {
     "fromAccount": "<valid account number from seed data>",
     "toAccount": "<different valid account number>",
     "amount": 50.00,
     "authID": "<authenticated user ID>"
   }
   ```
4. Verify HTTP 200 response with `InternalTransferResponse` containing:
   - `status: "SUCCESS"`
   - `transactionId: <UUID>`
   - matching account numbers and amount
5. Verify in MySQL `fund_transfer` table:
   - Record exists with `status='SUCCESS'`
   - `transaction_reference` matches the returned `transactionId`
6. Verify in MySQL `banking_core_transaction` table:
   - Two transaction records exist with the same `transaction_id`
   - One debit leg (negative amount) for fromAccount
   - One credit leg (positive amount) for toAccount
7. Verify account balances updated correctly in `banking_core_account` table.

#### Verification Test 2: Insufficient Funds Error
1. Send POST request to internal transfer endpoint with amount exceeding fromAccount balance.
2. Verify HTTP 400 response with error message containing "Insufficient funds".
3. Verify in MySQL `fund_transfer` table:
   - Record exists with `status='FAILED'`
4. Verify account balances remain unchanged.

#### Verification Test 3: Invalid Account Error
1. Send POST request to internal transfer endpoint with non-existent fromAccount or toAccount.
2. Verify HTTP 400 or 404 response with appropriate error message.
3. Verify in MySQL `fund_transfer` table:
   - Record exists with `status='FAILED'`

#### Verification Test 4: OpenAPI Documentation
1. Access OpenAPI spec at `http://localhost:8084/v3/api-docs` (Fund Transfer Service direct port) or via gateway-proxied path.
2. Verify `/api/v1/transfer/internal` endpoint is documented with:
   - POST method
   - Operation summary and description
   - Request schema for `InternalTransferRequest`
   - Response schema for `InternalTransferResponse`

### Verification Logs

Successful execution should produce logs in the fund transfer service container:
```
Processing internal transfer - from: {fromAccount}, to: {toAccount}, amount: {amount}
Created pending transfer record with ID: {id}
Invoking core banking for internal transfer execution
Internal transfer completed successfully - transactionId: {transactionId}
```

Failure execution should produce logs:
```
Processing internal transfer - from: {fromAccount}, to: {toAccount}, amount: {amount}
Created pending transfer record with ID: {id}
Internal transfer failed - from: {fromAccount}, to: {toAccount}, error: {errorMessage}
```

## Update Rules

### When requirements change
1. Update the requirement statement and source reference in the Requirement Inventory section.
2. Review impacted rows in the Trace Matrix and update Implementation Mapping, Inline Code Trace, and Verification Mapping columns.
3. Update inline requirement comments in code if the requirement ID changes or enforcement location changes.
4. Document the change rationale in the Notes column.

### When code is refactored (moved/renamed/split/merged)
1. Update Implementation Mapping and Inline Code Trace columns to reflect new file/symbol locations.
2. Update inline requirement markers in code to follow the owning logic.
3. If a module is split, document the new ownership in the Notes column and ensure inline markers are placed in each new owning location.
4. Do not leave stale file paths in the matrix.

### When tests are added
1. Update Verification Mapping column to reference the new test file and test method name.
2. Update Status to reflect improved verification coverage.
3. Add a note explaining the verification improvement (e.g., "Automated test added, replacing manual verification").

### When new related functionality is added
1. If new functionality implements an existing requirement in this matrix, add a new row or update the existing Implementation Mapping to include the new module/symbol.
2. If new functionality introduces new requirements, add them to the Requirement Inventory and create new matrix rows.
3. Ensure inline requirement markers are added to new code at appropriate ownership/enforcement points.

## Trace Matrix Location and Ownership

This traceability document is located at:
```
kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md
```

It should be maintained as part of any change to the internal transfer feature implementation. Updates to this document are required when:
- The internal transfer API contract changes
- New validation or business rules are added
- The orchestration flow changes
- Error handling behavior changes

## Related Documents

- [Internet Banking Backend PRD](internet-banking-backend-prd.md) - Parent PRD containing FR-006
- [Internet Banking Concept Microservices LLD](../../Architecture/internet-banking-concept-microservices-lld.md) - Low-level design describing transaction processing architecture
- [Feature Specs Index](index.md) - Parent index for feature specifications

## Quality Checklist

- [x] All requirements from work item inventoried
- [x] Trace matrix complete for all requirements
- [x] Inline requirement markers added at ownership/enforcement points
- [x] All mapped file paths exist in repository
- [x] Verification procedures documented for each requirement
- [x] Gaps explicitly identified (manual verification required due to no automated tests)
