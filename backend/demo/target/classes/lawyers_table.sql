-- PostgreSQL table for Lawyers
-- This table will be automatically created by Hibernate with ddl-auto=update
-- But you can use this script for manual creation or reference

CREATE TABLE IF NOT EXISTS lawyers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mobile_number VARCHAR(20) NOT NULL,
    aadhar_number VARCHAR(12) NOT NULL UNIQUE,
    aadhar_proof BYTEA,
    aadhar_proof_filename VARCHAR(255),
    bar_council_id VARCHAR(255) NOT NULL UNIQUE,
    bar_state VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    bar_certificate BYTEA,
    bar_certificate_filename VARCHAR(255),
    experience_years INTEGER NOT NULL,
    address TEXT NOT NULL,
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_lawyers_email ON lawyers(email);
CREATE INDEX IF NOT EXISTS idx_lawyers_aadhar ON lawyers(aadhar_number);
CREATE INDEX IF NOT EXISTS idx_lawyers_bar_council_id ON lawyers(bar_council_id);
CREATE INDEX IF NOT EXISTS idx_lawyers_specialization ON lawyers(specialization);
CREATE INDEX IF NOT EXISTS idx_lawyers_state ON lawyers(state);
CREATE INDEX IF NOT EXISTS idx_lawyers_city ON lawyers(city);

