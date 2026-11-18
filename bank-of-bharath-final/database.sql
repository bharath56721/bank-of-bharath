CREATE DATABASE IF NOT EXISTS bank_of_bharath;
USE bank_of_bharath;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_number VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  phone VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  balance DECIMAL(15,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_number VARCHAR(255) NOT NULL,
  type VARCHAR(2) NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  description VARCHAR(255),
  created_at DATETIME(6),
  INDEX idx_txn_account (account_number)
);
