-- Create database if not exists
SELECT 'CREATE DATABASE park_assembly'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'park_assembly')\gexec

-- Connect to the database
\c park_assembly

-- Create tables (if not using JPA auto-generation)
-- Note: With spring.jpa.hibernate.ddl-auto=update, Hibernate will create tables automatically
-- This script is mainly for creating the admin user

-- Insert admin user with default password 'admin'
-- Note: This password should be encoded with BCrypt in production
-- BCrypt hash for 'admin': $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a
INSERT INTO users (username, password, role, created_at)
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ADMIN', CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_cars_placa ON cars(placa);
CREATE INDEX IF NOT EXISTS idx_cars_data_entrada ON cars(data_entrada);
CREATE INDEX IF NOT EXISTS idx_cars_data_saida ON cars(data_saida);

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE park_assembly TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
