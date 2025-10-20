-- =====================================================
-- 1. Drop old tables (optional, for initial migration)
-- =====================================================
DROP TABLE IF EXISTS comment CASCADE;
DROP TABLE IF EXISTS parameters CASCADE;
DROP TABLE IF EXISTS reagent_used CASCADE;
DROP TABLE IF EXISTS test_order CASCADE;
DROP TABLE IF EXISTS test_result CASCADE;
DROP TABLE IF EXISTS test_result_reagent_used CASCADE;

-- =====================================================
-- 2. Create TABLE: parameters
-- =====================================================
CREATE TABLE parameters (
    id BIGSERIAL PRIMARY KEY,
    created_at DATE NOT NULL,
    updated_at DATE NOT NULL,
    name VARCHAR(255) NOT NULL,
    abbreviation VARCHAR(255),
    description VARCHAR(255),
    param_code VARCHAR(255) NOT NULL UNIQUE,
    min FLOAT8,
    max FLOAT8,
    status VARCHAR(255)
        CHECK (status IN ('ACTIVE','INACTIVE','DEPRECATED','DELETED')),
    unit VARCHAR(255)
        CHECK (unit IN (
                        'PERCENTAGE','CELSIUS','BPM','CELLS_PER_UL',
                        'MILLIONS_PER_UL','FL','PG','G_PER_DL'
            ))
);

-- =====================================================
-- 3. Create TABLE: reagent_used
-- =====================================================
CREATE TABLE reagent_used (
      id BIGSERIAL PRIMARY KEY,
      reagent_id BIGINT NOT NULL,
      quantity INTEGER NOT NULL,
      slot_number VARCHAR(255),
      used_at TIMESTAMP(6),
      updated_at TIMESTAMP(6) NOT NULL
);

-- =====================================================
-- 4. Create TABLE: test_order
-- =====================================================
CREATE TABLE test_order (
    id BIGSERIAL PRIMARY KEY,
    run_date DATE,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    created_by BIGINT NOT NULL,
    run_by BIGINT,
    patient_id BIGINT NOT NULL,
    external_medical_record_id BIGINT NOT NULL,
    status VARCHAR(255)
        CHECK (status IN (
                          'PENDING','ONGOING','WAITING','CANCELED',
                          'COMPLETED','REVIEWED','AI_REVIEWED'
            ))
);

-- =====================================================
-- 5. Create TABLE: test_result
-- =====================================================
CREATE TABLE test_result (
     id BIGSERIAL PRIMARY KEY,
     value FLOAT8 NOT NULL,
     created_at TIMESTAMP(6) NOT NULL,
     updated_at TIMESTAMP(6) NOT NULL,
     flag_status VARCHAR(255) NOT NULL,
     status VARCHAR(255) NOT NULL
         CHECK (status IN ('PENDING','COMPLETED','FAILED','CANCELLED','WAITING')),
     test_type VARCHAR(255) NOT NULL
         CHECK (test_type IN (
                              'CBC','HEMOGLOBIN','HEMATOCRIT','WBC_DIFF',
                              'PLATELET_COUNT','GLUCOSE','CHOLESTEROL','ALT','AST',
                              'CREATININE','BUN','NA','K','CL','HIV_ANTIBODY','HBSAG',
                              'ANA','CRP','RHEUMATOID_FACTOR','BACTERIAL_CULTURE','GRAM_STAIN',
                              'ANTIBIOTIC_SUSCEPTIBILITY','PT','INR','D_DIMER','URINE_ROUTINE',
                              'URINE_MICROSCOPY','CSF_ANALYSIS','BLOOD_TYPING','CROSSMATCHING',
                              'ANTIBODY_SCREENING'
             )),
     instrument_id BIGINT NOT NULL,
     parameter_id BIGINT NOT NULL,
     parameter_latest_snapshot_id BIGINT NOT NULL,
     test_order_id BIGINT NOT NULL
);

-- =====================================================
-- 6. Create TABLE: comment
-- =====================================================
CREATE TABLE comment (
     id BIGSERIAL PRIMARY KEY,
     created_at TIMESTAMP(6) NOT NULL,
     updated_at TIMESTAMP(6) NOT NULL,
     user_id BIGINT NOT NULL,
     comment_text TEXT NOT NULL,
     status VARCHAR(255)
         CHECK (status IN ('VISIBLE','HIDDEN','DELETED')),
     test_order_id BIGINT,
     test_result_id BIGINT
);

-- =====================================================
-- 7. Create TABLE: test_result_reagent_used
-- =====================================================
CREATE TABLE test_result_reagent_used (
      test_result_id BIGINT NOT NULL,
      reagent_used_id BIGINT NOT NULL
);

-- =====================================================
-- 8. Foreign keys
-- =====================================================
ALTER TABLE comment
    ADD CONSTRAINT fk_comment_test_order
        FOREIGN KEY (test_order_id) REFERENCES test_order;

ALTER TABLE comment
    ADD CONSTRAINT fk_comment_test_result
        FOREIGN KEY (test_result_id) REFERENCES test_result;

ALTER TABLE test_result
    ADD CONSTRAINT fk_test_result_parameter
        FOREIGN KEY (parameter_id) REFERENCES parameters;

ALTER TABLE test_result
    ADD CONSTRAINT fk_test_result_test_order
        FOREIGN KEY (test_order_id) REFERENCES test_order;

ALTER TABLE test_result_reagent_used
    ADD CONSTRAINT fk_tr_reagent_used
        FOREIGN KEY (reagent_used_id) REFERENCES reagent_used;

ALTER TABLE test_result_reagent_used
    ADD CONSTRAINT fk_tr_test_result
        FOREIGN KEY (test_result_id) REFERENCES test_result;
