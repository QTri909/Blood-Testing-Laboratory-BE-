-- =====================================================
-- SEED DATA FOR TEST ORDER SERVICE
-- =====================================================

-- 1️⃣ PARAMETERS
INSERT INTO parameters (created_at, updated_at, name, abbreviation, description, param_code, min, max, status, unit)
VALUES
    ('2025-01-01', '2025-01-01', 'Hemoglobin', 'HB', 'Hemoglobin concentration', 'PARAM_HB', 12.0, 17.5, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 'Hematocrit', 'HCT', 'Red blood cell volume', 'PARAM_HCT', 36.0, 50.0, 'ACTIVE', 'PERCENTAGE'),
    ('2025-01-01', '2025-01-01', 'WBC Count', 'WBC', 'White blood cell count', 'PARAM_WBC', 4.0, 11.0, 'ACTIVE', 'CELLS_PER_UL'),
    ('2025-01-01', '2025-01-01', 'Platelet Count', 'PLT', 'Platelet cell count', 'PARAM_PLT', 150.0, 450.0, 'ACTIVE', 'CELLS_PER_UL'),
    ('2025-01-01', '2025-01-01', 'Glucose', 'GLU', 'Blood glucose level', 'PARAM_GLU', 70.0, 140.0, 'ACTIVE', 'G_PER_DL');

-- 2️⃣ REAGENT_USED
INSERT INTO reagent_used (reagent_id, quantity, slot_number, used_at, updated_at)
VALUES
    (1, 5, 'A1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 10, 'B2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 8, 'C3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 6, 'D4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 7, 'E5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3️⃣ TEST_ORDER
INSERT INTO test_order (run_date, created_at, updated_at, created_by, run_by, patient_id, external_medical_record_id, status)
VALUES
    ('2025-10-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1001, 2001, 3001, 4001, 'PENDING'),
    ('2025-10-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1002, 2002, 3002, 4002, 'ONGOING'),
    ('2025-10-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1003, 2003, 3003, 4003, 'WAITING'),
    ('2025-10-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1004, 2004, 3004, 4004, 'COMPLETED'),
    ('2025-10-05', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1005, 2005, 3005, 4005, 'REVIEWED');

-- 4️⃣ TEST_RESULT
INSERT INTO test_result (
    value, created_at, updated_at, flag_status, status, test_type,
    instrument_id, parameter_id, parameter_latest_snapshot_id, test_order_id
)
VALUES
    (15.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NORMAL', 'COMPLETED', 'HEMOGLOBIN', 101, 1, 1, 1),
    (42.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NORMAL', 'COMPLETED', 'HEMATOCRIT', 102, 2, 2, 2),
    (8.5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'LOW', 'PENDING', 'WBC_DIFF', 103, 3, 3, 3),
    (210.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NORMAL', 'COMPLETED', 'PLATELET_COUNT', 104, 4, 4, 4),
    (110.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'HIGH', 'COMPLETED', 'GLUCOSE', 105, 5, 5, 5);

-- 5️⃣ COMMENT
INSERT INTO comment (created_at, updated_at, user_id, comment_text, status, test_order_id, test_result_id)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5001, 'Initial test completed successfully.', 'VISIBLE', 1, 1),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5002, 'Patient needs retest in 2 weeks.', 'VISIBLE', 2, 2),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5003, 'Result under review by senior doctor.', 'HIDDEN', 3, 3),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5004, 'Abnormal WBC count detected.', 'VISIBLE', 4, 4),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5005, 'Report verified by QA team.', 'VISIBLE', 5, 5);

-- 6️⃣ TEST_RESULT_REAGENT_USED
INSERT INTO test_result_reagent_used (test_result_id, reagent_used_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);
