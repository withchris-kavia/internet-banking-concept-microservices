-- Schema for Fund Transfer Service test database
-- REQ: FR-006 - Fund transfers can be initiated and persisted

CREATE TABLE IF NOT EXISTS fund_transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_reference VARCHAR(255),
    from_account VARCHAR(255),
    to_account VARCHAR(255),
    amount DECIMAL(19, 2),
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
