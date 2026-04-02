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

```json
{
  "errorCode": "INSUFFICIENT_FUNDS",
  "message": "Insufficient funds in the account 123456789"
}
```

#### 400 Bad Request - Invalid Account

```json
{
  "errorCode": "ENTITY_NOT_FOUND",
  "message": "Account not found"
}
```

#### 400 Bad Request - Validation Error

```json
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Invalid request parameters"
}
```

#### 401 Unauthorized

```json
{
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

#### 500 Internal Server Error

```json
{
  "errorCode": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred while processing the transfer"
}
```

## Request Flow

1. Client sends a POST request (with JWT) to the API Gateway.
2. The gateway validates the JWT and routes to the Fund Transfer Service.
3. The Fund Transfer Service persists a PENDING record, then calls Core Banking.
4. Core Banking validates accounts, updates balances atomically, and writes ledger entries.
5. The Fund Transfer Service updates the local record to SUCCESS (or FAILED on error) and returns the response.

## Related Documents

- [Internal Transfer API - Quick Start Guide](internal-transfer-quick-start.md)
- [Internal Transfer API - Implementation Summary](internal-transfer-implementation-summary.md)
- [Internal Transfer API - Requirement Traceability](../Specs/FeatureSpecs/internal-transfer-traceability.md)
