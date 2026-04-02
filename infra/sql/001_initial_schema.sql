-- 001_initial_schema.sql
-- First database bootstrap script. Run order: 001, then 002, ... if you add more files later.
-- Docker: copied/mounted into docker-entrypoint-initdb.d — runs only on empty data directory.
-- Manual: psql -U vehicle -d vehicle -f 001_initial_schema.sql
--
CREATE TABLE brand (
    id         BIGSERIAL PRIMARY KEY,
    fipe_code  VARCHAR(32)  NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_brand_fipe_code UNIQUE (fipe_code)
);

CREATE TABLE vehicle (
    id               BIGSERIAL PRIMARY KEY,
    brand_id         BIGINT       NOT NULL REFERENCES brand (id) ON DELETE RESTRICT,
    fipe_model_code  INTEGER      NOT NULL,
    model_name       VARCHAR(500) NOT NULL,
    notes            TEXT,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_vehicle_brand_fipe_model UNIQUE (brand_id, fipe_model_code)
);

CREATE INDEX idx_vehicle_brand_id ON vehicle (brand_id);