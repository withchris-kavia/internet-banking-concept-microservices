# Internal Transfer API Specification

## Overview

This document provides the detailed API specification for the internal transfer endpoint in the Fund Transfer microservice.

## Endpoint Details

### Internal Transfer

**Endpoint:** `POST /api/v1/transfer/internal`

**Description:** Transfer funds between two internal bank accounts within the same banking system.

**Authentication:** Required (JWT Bearer token)

**Service:** Fund Transfer Service (Port 8084, proxied via API Gateway on port 8082)

## Request Specification

### Headers

| Header | Required | Description | Example |
|--------|----------|-------------|---------|
| `Authorization` | Yes | JWT Bearer token | `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` |
| `Content-Type` | Yes | Request content type | `application/json` |

### Request Body

**Content-Type:** `application/json`

**Schema:**

| Field | Type | Required | Description | Validation | Example |
|-------|------|----------|-------------|------------|---------|
| `fromAccount` | string | Yes | Source account number | Must be a valid existing account | `"123456789"` |
| `toAccount` | string | Yes | Destination account number | Must be a valid existing account, different from source | `"987654321"` |
| `amount` | number | Yes | Transfer amount | Must be positive, source account must have sufficient balance | `100.00` |
| `authID` | string | Yes | Authenticated user identifier | Must match authenticated user from JWT | `"user@example.com"` |
| `description` | string | No | Optional transfer memo or description | Max 255 characters | `"Payment for services"` |

**Example Request Body:**

```json
{
  "fromAccount": "123456789",
  "toAccount": "987654321",
  "amount": 100.00,
  "authID": "user@example.com",
  "description": "Monthly transfer"
}
```

## Response Specification

### Success Response (200 OK)

**Content-Type:** `application/json`

**Schema:**

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `message` | string | Success message | `"Internal transfer completed successfully"` |
| `transactionId` | string | Unique transaction identifier (UUID) | `"550e8400-e29b-41d4-a716-446655440000"` |
| `fromAccount` | string | Source account number | `"123456789"` |
| `toAccount` | string | Destination account number | `"987654321"` |
| `amount` | number | Transfer amount | `100.00` |
| `status` | string | Transaction status | `"SUCCESS"` |

**Example Success Response:**

```json
{
  "message": "Internal transfer completed successfully",
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "fromAccount": "123456789",
  "toAccount": "987654321",
  "amount": 100.00,
  "status": "SUCCESS"
}
```

### Error Responses

#### 400 Bad Request - Insufficient Funds

**Scenario:** Source account does not have sufficient balance for the transfer amount.

```json
{
  "errorCode": "INSUFFICIENT_FUNDS",
  "message": "Insufficient funds in the account 123456789"
}
```

#### 400 Bad Request - Invalid Account

**Scenario:** Source or destination account does not exist.

```json
{
  "errorCode": "ENTITY_NOT_FOUND",
  "message": "Account not found"
}
```

#### 400 Bad Request - Validation Error

**Scenario:** Request validation failed (e.g., negative amount, same source and destination account).

```json
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Invalid request parameters"
}
```

#### 401 Unauthorized

**Scenario:** Missing or invalid JWT token.

```json
{
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

#### 500 Internal Server Error

**Scenario:** Unexpected system error during processing.

```json
{
  "errorCode": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred while processing the transfer"
}
```

## Request Flow

1. **Client → API Gateway** (Port 8082)
   - Client sends POST request with JWT token
   - Gateway validates JWT and extracts principal
   - Gateway adds `X-Auth-Id` header with authenticated user ID
   - Gateway routes to Fund Transfer Service

2. **API Gateway → Fund Transfer Service** (Port 8084)
   - Request arrives at `FundTransferController.internalTransfer()`
   - Controller validates request body
   - Controller delegates to `FundTransferService.internalTransfer()`

3. **Fund Transfer Service Processing**
   - Service creates `FundTransferEntity` with status PENDING
   - Service persists entity to `fund_transfer` table
   - Service converts request to core banking format
   - Service calls Core Banking Service via Feign client

4. **Fund Transfer Service → Core Banking Service** (Port 8092)
   - Request arrives at `/api/v1/transaction/fund-transfer`
   - Core banking validates account existence
   - Core banking validates sufficient balance
   - Core banking updates account balances atomically
   - Core banking creates two transaction ledger entries (debit and credit)
   - Core banking returns transaction ID

5. **Fund Transfer Service Response**
   - Service updates `FundTransferEntity` with transaction reference and SUCCESS status
   - Service builds `InternalTransferResponse`
   - Service returns response to API Gateway

6. **API Gateway → Client**
   - Gateway returns response to client with 200 OK

## Error Flow

If any step fails (e.g., insufficient funds, account not found):

1. Core Banking Service throws exception (e.g., `InsufficientFundsException`)
2. Exception propagates through Feign client to Fund Transfer Service
3. Fund Transfer Service catches exception in try-catch block
4. Fund Transfer Service updates `FundTransferEntity` status to FAILED
5. Fund Transfer Service logs error with context
6. Fund Transfer Service re-throws exception
7. Spring exception handler converts to appropriate HTTP error response
8. API Gateway returns error response to client

## Database Impact

### fund_transfer Table

A new record is created for each internal transfer request:

| Column | Value | Notes |
|--------|-------|-------|
| `id` | Auto-generated | Primary key |
| `from_account` | Request.fromAccount | Source account number |
| `to_account` | Request.toAccount | Destination account number |
| `amount` | Request.amount | Transfer amount |
| `status` | PENDING → SUCCESS/FAILED | Updated based on outcome |
| `transaction_reference` | UUID from core banking | Set on success |
| `created_date` | Auto-generated | Audit timestamp |
| `last_modified_date` | Auto-generated | Audit timestamp |

### banking_core_account Table (in Core Banking Service)

Account balances are updated atomically:

- **Source Account:** `actual_balance` and `available_balance` decreased by transfer amount
- **Destination Account:** `actual_balance` and `available_balance` increased by transfer amount

### banking_core_transaction Table (in Core Banking Service)

Two transaction records are created:

1. **Debit Leg** (source account):
   - `transaction_type`: FUND_TRANSFER
   - `account`: Source account entity
   - `amount`: Negative (debit)
   - `reference_number`: Destination account number
   - `transaction_id`: UUID

2. **Credit Leg** (destination account):
   - `transaction_type`: FUND_TRANSFER
   - `account`: Destination account entity
   - `amount`: Positive (credit)
   - `reference_number`: Destination account number
   - `transaction_id`: Same UUID as debit leg

## Testing with Postman/cURL

### Using API Gateway (Recommended)

```bash
curl -X POST http://localhost:8082/api/v1/transfer/internal \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccount": "123456789",
    "toAccount": "987654321",
    "amount": 100.00,
    "authID": "user@example.com"
  }'
```

### Direct to Fund Transfer Service (Development Only)

```bash
curl -X POST http://localhost:8084/api/v1/transfer/internal \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccount": "123456789",
    "toAccount": "987654321",
    "amount": 100.00,
    "authID": "user@example.com"
  }'
```

## OpenAPI/Swagger Documentation

The internal transfer API is documented via OpenAPI annotations and can be accessed at:

- **Fund Transfer Service Direct:** `http://localhost:8084/v3/api-docs`
- **Swagger UI (if enabled):** `http://localhost:8084/swagger-ui.html`

## Related Endpoints

### List All Transfers

**Endpoint:** `GET /api/v1/transfer`

Returns paginated list of all fund transfers (including internal transfers).

**Query Parameters:**
- `page`: Page number (default 0)
- `size`: Page size (default 20)
- `sort`: Sort field and direction (e.g., `createdDate,desc`)

### Legacy Fund Transfer

**Endpoint:** `POST /api/v1/transfer`

Original fund transfer endpoint that accepts `FundTransferRequest`. The internal transfer endpoint is semantically clearer for account-to-account transfers.

## Differences from Legacy Fund Transfer Endpoint

| Aspect | Legacy `/api/v1/transfer` | New `/api/v1/transfer/internal` |
|--------|---------------------------|----------------------------------|
| **Request DTO** | `FundTransferRequest` | `InternalTransferRequest` |
| **Response DTO** | `FundTransferResponse` | `InternalTransferResponse` |
| **Response Details** | Basic (message, transactionId) | Enhanced (includes accounts, amount, status) |
| **Semantic Clarity** | Generic transfer | Explicitly internal account transfer |
| **Error Handling** | Basic | Enhanced with FAILED status tracking |
| **Documentation** | Basic | Enhanced with field descriptions and examples |

## Security Considerations

1. **Authentication Required:** All requests must include valid JWT token
2. **Authorization:** Core Banking Service should validate that the authenticated user has permission to transfer from the source account (implementation in core banking)
3. **Account Ownership:** System should verify user owns or has permission for source account
4. **Rate Limiting:** Consider implementing rate limiting to prevent abuse
5. **Audit Trail:** All transfers are logged with context and persisted with audit timestamps
6. **Transaction Atomicity:** Core banking uses `@Transactional` to ensure atomic balance updates

## Performance Considerations

- **Database Round Trips:** 3 writes to `fund_transfer` table (create PENDING, update to SUCCESS/FAILED)
- **Inter-Service Call:** 1 synchronous Feign call to Core Banking Service
- **Core Banking Writes:** 2 account updates + 2 transaction ledger entries
- **Typical Response Time:** < 500ms (depending on network and database latency)

## Monitoring and Observability

Key log messages to monitor:

- `Processing internal transfer - from: {}, to: {}, amount: {}`
- `Created pending transfer record with ID: {}`
- `Invoking core banking for internal transfer execution`
- `Internal transfer completed successfully - transactionId: {}`
- `Internal transfer failed - from: {}, to: {}, error: {}`

Metrics to track:

- Request count and rate
- Success rate vs failure rate
- Response time percentiles (p50, p95, p99)
- Error types and frequencies
- Transfer amount distribution

## Future Enhancements

1. **Transfer Limits:** Per-account daily and per-transaction limits
2. **Transfer Scheduling:** Schedule transfers for future dates
3. **Recurring Transfers:** Set up automatic recurring transfers
4. **Transfer Templates:** Save frequent transfer recipients
5. **Batch Transfers:** Process multiple transfers in one request
6. **Transfer Reversal:** API to reverse/cancel transfers within time window
7. **Transfer Notifications:** Push notifications on transfer completion
8. **Multi-Currency Support:** Handle transfers between different currency accounts
9. **Transfer Fees:** Calculate and apply transfer fees based on rules
10. **Transfer Approval Workflow:** Require approval for high-value transfers
