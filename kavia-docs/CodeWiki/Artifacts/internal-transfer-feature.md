# Internal Transfer API Feature

## Overview

This document describes the Internal Transfer API added to the Fund Transfer microservice. This feature enables customers to transfer funds between their own accounts or to other accounts within the same banking system.

## Feature Description

The Internal Transfer API provides a dedicated endpoint for processing transfers between accounts that exist within the core banking system. It follows the same orchestration pattern as the existing fund transfer functionality but provides a clearer semantic API for internal account-to-account transfers.

## API Endpoint

### POST `/api/v1/transfer/internal`

Process an internal transfer between two bank accounts.

**Request Body:**

```json
{
  "fromAccount": "123456789",
  "toAccount": "987654321",
  "amount": 100.00,
  "authID": "user@example.com",
  "description": "Optional transfer memo"
}
```

**Response:**

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

**Status Codes:**

- `200 OK`: Transfer completed successfully
- `400 Bad Request`: Insufficient funds, invalid account, or validation error
- `401 Unauthorized`: Missing or invalid JWT token
- `500 Internal Server Error`: Unexpected system error

## Implementation Details

The internal transfer request is validated by the controller, persisted as a PENDING `fund_transfer` record, executed via the Core Banking fund transfer endpoint, and then updated to SUCCESS with a transaction reference. If any step fails, the local record is updated to FAILED and the error is propagated to the caller.

## Integration with Existing System

The internal transfer feature reuses existing platform components, including Core Banking’s transaction endpoint, service-to-service communication (Feign), the existing `fund_transfer` persistence table, gateway routing/security, and Eureka-based discovery.

## Security

The internal transfer API follows the same security model as other fund transfer endpoints. Requests are authenticated via JWT at the API Gateway and the authenticated identity is propagated to downstream services.

## Testing

Manual verification procedures are captured in the traceability matrix:

- [Internal Transfer API - Requirement Traceability](../Specs/FeatureSpecs/internal-transfer-traceability.md)

## Related Documentation

- [Internal Transfer API Specification](internal-transfer-api-spec.md)
- [Internal Transfer API - Implementation Summary](internal-transfer-implementation-summary.md)
- [Internal Transfer API - Requirement Traceability](../Specs/FeatureSpecs/internal-transfer-traceability.md)
