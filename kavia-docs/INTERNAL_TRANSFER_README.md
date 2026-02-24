# Internal Transfer API - Quick Start Guide

## What is the Internal Transfer API?

The Internal Transfer API is a new endpoint in the Fund Transfer microservice that enables transferring funds between accounts within the same banking system. It provides a clear, well-documented interface for internal account-to-account transfers.

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

**Solution:** Verify the source account has sufficient balance.

### Account Not Found (HTTP 400/404)

```json
{
  "errorCode": "ENTITY_NOT_FOUND",
  "message": "Account not found"
}
```

**Solution:** Verify both account numbers exist in the system.

### Unauthorized (HTTP 401)

```json
{
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

**Solution:** Ensure you're sending a valid JWT token in the Authorization header.

## How It Works

1. **Request Received:** Your request hits the API Gateway
2. **Authentication:** Gateway validates your JWT token
3. **Routing:** Request is routed to Fund Transfer Service
4. **Persistence:** A PENDING transfer record is created
5. **Validation:** Core Banking validates accounts and balance
6. **Execution:** Balances are updated atomically
7. **Ledger:** Transaction entries are created (debit + credit)
8. **Update:** Transfer record updated to SUCCESS
9. **Response:** Transaction details returned to you

## Testing in Your Local Environment

### 1. Start the Services

```bash
cd docker-compose
docker-compose up -d
```

Wait for all services to be healthy (~2 minutes).

### 2. Get Test Accounts

The system comes with pre-seeded test accounts. Check the seed data:

```bash
docker exec -it mysql_javatodev_app mysql -uroot -ppassword banking_core -e \
  "SELECT number, actual_balance FROM banking_core_account LIMIT 5;"
```

### 3. Get a JWT Token

Use Postman or the provided Postman collection to authenticate via Keycloak and obtain a JWT token.

Keycloak is available at: `http://localhost:8080`

### 4. Make Your First Transfer

```bash
TOKEN="your_token_here"
FROM_ACCOUNT="100000001"  # Use a real account from step 2
TO_ACCOUNT="100000002"    # Use a different real account

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

### 5. Verify the Transfer

Check the database:

```bash
# View the transfer record
docker exec -it mysql_javatodev_app mysql -uroot -ppassword banking_core -e \
  "SELECT * FROM fund_transfer ORDER BY created_date DESC LIMIT 1;"

# View updated balances
docker exec -it mysql_javatodev_app mysql -uroot -ppassword banking_core -e \
  "SELECT number, actual_balance FROM banking_core_account WHERE number IN ('$FROM_ACCOUNT', '$TO_ACCOUNT');"

# View transaction ledger
docker exec -it mysql_javatodev_app mysql -uroot -ppassword banking_core -e \
  "SELECT transaction_id, transaction_type, amount, reference_number FROM banking_core_transaction ORDER BY created_date DESC LIMIT 2;"
```

## Using Postman

### Import the Collection

The repository includes a Postman collection: `postman_collection/JAVA_TO_DEV_MICROSERVICES.postman_collection.json`

### Add Internal Transfer Request

1. Create a new request in Postman
2. Set method to `POST`
3. Set URL to `{{gateway_url}}/api/v1/transfer/internal`
4. In Headers tab, add:
   - `Authorization: Bearer {{access_token}}`
   - `Content-Type: application/json`
5. In Body tab, select `raw` and `JSON`, then add:

```json
{
  "fromAccount": "100000001",
  "toAccount": "100000002",
  "amount": 50.00,
  "authID": "{{user_email}}",
  "description": "Test internal transfer"
}
```

6. Send the request

## Viewing API Documentation

The internal transfer API is documented via OpenAPI/Swagger:

### OpenAPI JSON

```bash
# Fund Transfer Service (direct)
curl http://localhost:8084/v3/api-docs | jq .

# Or save to file
curl http://localhost:8084/v3/api-docs > fund-transfer-openapi.json
```

### Swagger UI (if enabled)

```
http://localhost:8084/swagger-ui.html
```

Look for the "Internal Account Transfer" operation under the "Fund Transfer API" tag.

## Monitoring and Debugging

### View Service Logs

```bash
# All logs
docker-compose logs -f internet-banking-fund-transfer-service

# Just errors
docker-compose logs -f internet-banking-fund-transfer-service | grep ERROR

# Filter for internal transfers
docker-compose logs -f internet-banking-fund-transfer-service | grep "internal transfer"
```

### Key Log Messages

**Success:**
```
Processing internal transfer - from: 100000001, to: 100000002, amount: 50.00
Created pending transfer record with ID: 123
Invoking core banking for internal transfer execution
Internal transfer completed successfully - transactionId: 550e8400-e29b-41d4-a716-446655440000
```

**Failure:**
```
Processing internal transfer - from: 100000001, to: 100000002, amount: 999999.00
Internal transfer failed - from: 100000001, to: 100000002, error: Insufficient funds in the account 100000001
```

### Check Service Health

```bash
# Fund Transfer Service health
curl http://localhost:8084/actuator/health

# Core Banking Service health
curl http://localhost:8092/actuator/health
```

## Differences from Legacy Transfer Endpoint

| Aspect | Legacy `/api/v1/transfer` | New `/api/v1/transfer/internal` |
|--------|---------------------------|----------------------------------|
| **Response Detail** | Basic (message + txnId) | Enhanced (+ accounts, amount, status) |
| **Semantic Clarity** | Generic transfer | Explicitly internal transfer |
| **Error Tracking** | Basic | Enhanced with FAILED status |
| **Documentation** | Minimal | Comprehensive with examples |

Both endpoints use the same underlying core banking service, so behavior is consistent.

## Troubleshooting

### "invalid source release: 21" Build Error

This error occurs if your Java version is less than 21. The project requires Java 21.

**Solution:**
- Install Java 21 (OpenJDK 21 recommended)
- Or use Docker to build: `docker-compose build internet-banking-fund-transfer-service`

### "Connection refused" When Calling the API

**Possible causes:**
1. Services not fully started
2. Eureka discovery not complete
3. Database not ready

**Solution:**
```bash
# Check all services are running
docker-compose ps

# Check Fund Transfer Service logs
docker-compose logs internet-banking-fund-transfer-service

# Restart if needed
docker-compose restart internet-banking-fund-transfer-service
```

### Transfer Record Shows PENDING Forever

This indicates the transfer failed but the exception wasn't caught properly.

**Debug:**
```bash
# Check service logs for exceptions
docker-compose logs internet-banking-fund-transfer-service | grep -A 10 "Processing internal transfer"

# Check if Core Banking Service is reachable
docker-compose logs core-banking-service
```

### Account Balance Not Updated

If the transfer shows SUCCESS but balance didn't change:

1. Check you're querying the correct accounts
2. Verify transaction ledger entries exist
3. Check for database transaction rollback in Core Banking logs

```bash
docker exec -it mysql_javatodev_app mysql -uroot -ppassword banking_core -e \
  "SELECT * FROM banking_core_transaction WHERE transaction_id = 'YOUR_TRANSACTION_ID';"
```

## Further Reading

- **Full API Specification:** `kavia-docs/internal-transfer-api-spec.md`
- **Feature Documentation:** `kavia-docs/internal-transfer-feature.md`
- **Implementation Summary:** `kavia-docs/internal-transfer-implementation-summary.md`
- **Requirement Traceability:** `kavia-docs/CodeWiki/Specs/FeatureSpecs/internal-transfer-traceability.md`
- **System Architecture:** `kavia-docs/CodeWiki/Architecture/internet-banking-concept-microservices-lld.md`

## Support

For questions or issues:
1. Check the troubleshooting section above
2. Review the service logs
3. Inspect the database records
4. Refer to the detailed documentation files listed above

## Contributing

When modifying the internal transfer feature:
1. Update code with proper documentation
2. Add inline requirement markers (REQ: FR-006)
3. Update the traceability matrix
4. Test manually with success and failure scenarios
5. Update relevant documentation files
