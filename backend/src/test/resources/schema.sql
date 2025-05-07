CREATE SCHEMA IF NOT EXISTS public;

DROP TABLE IF EXISTS cpp_source_files;

CREATE TABLE cpp_source_files
(
    id            SERIAL PRIMARY KEY,
    filename      VARCHAR(255) NOT NULL,
    source_code   TEXT         NOT NULL,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
