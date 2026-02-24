# Internal Transfer API Feature

## Overview

This document describes the new Internal Transfer API added to the Fund Transfer microservice. This feature enables customers to transfer funds between their own accounts or to other accounts within the same banking system.

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

### Flow

1. **Request Reception**: Controller receives and validates the internal transfer request
2. **Persistence**: Service persists a PENDING transfer record in the local database
3. **Core Banking Call**: Service converts request to core banking format and invokes the core banking fund transfer endpoint
4. **Balance Validation**: Core banking validates account existence and sufficient funds
5. **Transaction Execution**: Core banking atomically updates account balances and creates transaction ledger entries
6. **Status Update**: Service updates local transfer record to SUCCESS with transaction reference
7. **Response**: Service returns detailed transfer response to caller

### Error Handling

If any step fails (e.g., insufficient funds, account not found, system error):
- The local transfer record is updated to FAILED status
- The error is logged with context (accounts, amount, error message)
- The original exception is propagated to the caller

This ensures that failed transfer attempts are tracked and can be audited.

### Components Modified/Added

**New Files:**
- `InternalTransferRequest.java` - Request DTO
- `InternalTransferResponse.java` - Response DTO

**Modified Files:**
- `FundTransferController.java` - Added `internalTransfer` endpoint
- `FundTransferService.java` - Added `internalTransfer` business logic
- `TransactionStatus.java` - Added `FAILED` status enum value

## Integration with Existing System

The internal transfer feature integrates seamlessly with existing components:

- **Core Banking Service**: Reuses the existing `/api/v1/transaction/fund-transfer` endpoint for actual fund movement
- **Feign Client**: Uses the existing `BankingCoreFeignClient` for inter-service communication
- **Database**: Uses the existing `fund_transfer` table via `FundTransferRepository`
- **API Gateway**: Routed through the gateway with existing security and authentication
- **Service Discovery**: Uses Eureka for service-to-service resolution

## Security

The internal transfer API follows the same security model as other fund transfer endpoints:
- JWT-based authentication required at the API Gateway
- Authenticated user ID propagated via `X-Auth-Id` header
- Authorization enforced by core banking based on account ownership (implementation in core banking service)

## Testing

Manual verification procedures are documented in the traceability matrix:
- [Internal Transfer Traceability](kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md)

Verification includes:
- Successful transfer between valid accounts
- Insufficient funds error handling
- Invalid account error handling
- OpenAPI documentation validation

## OpenAPI Documentation

The internal transfer API is fully documented with OpenAPI/Swagger annotations:
- Operation summary and description
- Request schema with field descriptions and examples
- Response schema with field descriptions
- Can be viewed at `/v3/api-docs` endpoint of the Fund Transfer Service

## Future Enhancements

Potential improvements for future iterations:
1. **Transfer Limits**: Add configurable daily/transaction limits per account
2. **Transfer Scheduling**: Support for scheduled/recurring transfers
3. **Transfer History**: Enhanced querying and filtering of transfer history
4. **Notifications**: Integrate with notification service (when available) to send transfer confirmations
5. **Batch Transfers**: Support for processing multiple transfers in a single request
6. **Transfer Reversal**: API for reversing/canceling transfers within a time window

## Related Documentation

- [Internet Banking Backend PRD](kavia-docs/CodeWiki/Specs/FeatureSpecs/internet-banking-backend-prd.md)
- [Internal Transfer Traceability Matrix](kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md)
- [Internet Banking Concept Microservices LLD](kavia-docs/CodeWiki/Architecture/internet-banking-concept-microservices-lld.md)
