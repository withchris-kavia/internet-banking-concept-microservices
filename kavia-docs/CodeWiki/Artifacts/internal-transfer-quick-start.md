# Internal Transfer API - Quick Start Guide

## What is the Internal Transfer API?

The Internal Transfer API is a Fund Transfer microservice endpoint that enables transferring funds between accounts within the same banking system. It provides a clear, documented interface for internal account-to-account transfers.

## Quick Example

### Using cURL with API Gateway

```bash
# First, get a JWT token from Keycloak
TOKEN="your_jwt_token_here"

# Make an internal transfer
curl -X POST http://localhost:8082/api/v1/transfer/internal \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccount": "100000001",
    "toAccount": "100000002",
    "amount": 50.00,
    "authID": "user@example.com",
    "description": "Test transfer"
  }'
```

### Expected Response

```json
{
  "message": "Internal transfer completed successfully",
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "fromAccount": "100000001",
  "toAccount": "100000002",
  "amount": 50.00,
  "status": "SUCCESS"
}
```

## API Endpoint Details

- **URL:** `/api/v1/transfer/internal`
- **Method:** `POST`
- **Authentication:** Required (JWT Bearer token)
- **Content-Type:** `application/json`

### Request Body Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `fromAccount` | string | Yes | Source account number |
| `toAccount` | string | Yes | Destination account number |
| `amount` | number | Yes | Transfer amount (must be positive) |
| `authID` | string | Yes | Authenticated user identifier |
| `description` | string | No | Optional transfer memo |

### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `message` | string | Success message |
| `transactionId` | string | Unique transaction UUID |
| `fromAccount` | string | Source account number |
| `toAccount` | string | Destination account number |
| `amount` | number | Transfer amount |
| `status` | string | Transfer status (SUCCESS) |

## Common Error Scenarios

### Insufficient Funds (HTTP 400)

```json
{
  "errorCode": "INSUFFICIENT_FUNDS",
  "message": "Insufficient funds in the account 100000001"
}
```

Verify the source account has sufficient balance.

### Account Not Found (HTTP 400/404)

```json
{
  "errorCode": "ENTITY_NOT_FOUND",
  "message": "Account not found"
}
```

Verify both account numbers exist in the system.

### Unauthorized (HTTP 401)

```json
{
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

Ensure you're sending a valid JWT token in the Authorization header.

## How It Works

The request is received by the API Gateway, authenticated via JWT, routed to the Fund Transfer Service, persisted as a PENDING transfer, validated and executed via Core Banking, then updated to SUCCESS (or FAILED on error) before returning a detailed response.

## Testing in Your Local Environment

### 1. Start the Services

```bash
cd docker-compose
docker-compose up -d
```

### 2. Get Test Accounts

```bash
docker exec -it mysql_javatodev_app mysql -uroot -ppassword banking_core -e \
  "SELECT number, actual_balance FROM banking_core_account LIMIT 5;"
```

### 3. Make Your First Transfer

```bash
TOKEN="your_token_here"
FROM_ACCOUNT="100000001"
TO_ACCOUNT="100000002"

curl -X POST http://localhost:8082/api/v1/transfer/internal \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"fromAccount\": \"$FROM_ACCOUNT\",
    \"toAccount\": \"$TO_ACCOUNT\",
    \"amount\": 25.00,
    \"authID\": \"test@example.com\"
  }"
```

### 4. Verify the Transfer

```bash
docker exec -it mysql_javatodev_app mysql -uroot -ppassword banking_core -e \
  "SELECT * FROM fund_transfer ORDER BY created_date DESC LIMIT 1;"

docker exec -it mysql_javatodev_app mysql -uroot -ppassword banking_core -e \
  "SELECT number, actual_balance FROM banking_core_account WHERE number IN ('$FROM_ACCOUNT', '$TO_ACCOUNT');"
```

## Viewing API Documentation

### OpenAPI JSON

```bash
curl http://localhost:8084/v3/api-docs | jq .
```

### Swagger UI (if enabled)

```text
http://localhost:8084/swagger-ui.html
```

## Further Reading

- **Internal Transfer API Specification:** [CodeWiki Artifacts: Internal Transfer API Specification](internal-transfer-api-spec.md)
- **Feature Documentation:** [CodeWiki Artifacts: Internal Transfer API Feature](internal-transfer-feature.md)
- **Implementation Summary:** [CodeWiki Artifacts: Internal Transfer API - Implementation Summary](internal-transfer-implementation-summary.md)
- **Requirement Traceability:** [CodeWiki Specs: Internal Transfer Traceability](../Specs/FeatureSpecs/internal-transfer-traceability.md)
