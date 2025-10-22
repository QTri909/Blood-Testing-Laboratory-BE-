-- =========================================================
-- Flyway Migration: Seed initial data
-- Tables: parameters, reagent_used, synced_configurations
-- =========================================================

-- =========================================================
-- Table: parameters
-- =========================================================
INSERT INTO parameters (created_at, updated_at, createdBy, updatedBy, name, param_code, abbreviation, description, min, max, status, unit)
VALUES
    ('2025-01-01', '2025-01-01', 1, 1, 'Hemoglobin', 'HB001', 'Hb', 'Concentration of hemoglobin in blood', 12.0, 17.5, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Hematocrit', 'HCT001', 'HCT', 'Proportion of blood volume occupied by red blood cells', 36.0, 50.0, 'ACTIVE', 'PERCENTAGE'),
    ('2025-01-01', '2025-01-01', 1, 1, 'White Blood Cell Count', 'WBC001', 'WBC', 'Total white blood cells per microliter', 4000.0, 11000.0, 'ACTIVE', 'CELLS_PER_UL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Platelet Count', 'PLT001', 'PLT', 'Platelet count per microliter', 150000.0, 450000.0, 'ACTIVE', 'CELLS_PER_UL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Mean Corpuscular Volume', 'MCV001', 'MCV', 'Average volume of a red blood cell', 80.0, 100.0, 'ACTIVE', 'FL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Mean Corpuscular Hemoglobin', 'MCH001', 'MCH', 'Average amount of hemoglobin per red blood cell', 26.0, 34.0, 'ACTIVE', 'PG'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Glucose', 'GLU001', 'GLU', 'Blood glucose level', 70.0, 110.0, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Creatinine', 'CRE001', 'CRE', 'Serum creatinine level', 0.6, 1.3, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Sodium', 'NA001', 'Na', 'Serum sodium concentration', 135.0, 145.0, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Potassium', 'K001', 'K', 'Serum potassium concentration', 3.5, 5.1, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'Cholesterol', 'CHL001', 'CHOL', 'Serum cholesterol level', 120.0, 200.0, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'ALT', 'ALT001', 'ALT', 'Alanine transaminase enzyme activity', 7.0, 55.0, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'AST', 'AST001', 'AST', 'Aspartate transaminase enzyme activity', 8.0, 48.0, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'BUN', 'BUN001', 'BUN', 'Blood urea nitrogen', 7.0, 20.0, 'ACTIVE', 'G_PER_DL'),
    ('2025-01-01', '2025-01-01', 1, 1, 'CRP', 'CRP001', 'CRP', 'C-reactive protein level', 0.0, 10.0, 'ACTIVE', 'G_PER_DL');

-- =========================================================
-- Table: reagent_used
-- =========================================================
INSERT INTO reagent_used (quantity, reagent_id, updated_at, used_at, slot_number)
VALUES
    (50, 1001, now(), now(), 'A1'),
    (30, 1002, now(), now(), 'A2'),
    (20, 1003, now(), now(), 'A3'),
    (10, 1004, now(), now(), 'A4'),
    (15, 1005, now(), now(), 'A5'),
    (60, 1006, now(), now(), 'B1'),
    (25, 1007, now(), now(), 'B2'),
    (40, 1008, now(), now(), 'B3'),
    (35, 1009, now(), now(), 'C1'),
    (28, 1010, now(), now(), 'C2'),
    (18, 1011, now(), now(), 'C3'),
    (12, 1012, now(), now(), 'C4'),
    (45, 1013, now(), now(), 'C5'),
    (33, 1014, now(), now(), 'D1'),
    (22, 1015, now(), now(), 'D2');

-- =========================================================
-- Table: synced_configurations
-- =========================================================
INSERT INTO synced_configurations (id, minValue, maxValue, syncedAt, configKey, description, status, unit)
VALUES
    (1, 70.0, 110.0, now(), 'GLU_THRESHOLD', 'Normal blood glucose threshold', 'ACTIVE', 'G_PER_DL'),
    (2, 36.0, 50.0, now(), 'HCT_RANGE', 'Normal hematocrit range', 'ACTIVE', 'PERCENTAGE'),
    (3, 12.0, 17.5, now(), 'HB_RANGE', 'Normal hemoglobin range', 'ACTIVE', 'G_PER_DL'),
    (4, 135.0, 145.0, now(), 'NA_RANGE', 'Normal sodium range', 'ACTIVE', 'G_PER_DL'),
    (5, 3.5, 5.1, now(), 'K_RANGE', 'Normal potassium range', 'ACTIVE', 'G_PER_DL'),
    (6, 0.6, 1.3, now(), 'CRE_RANGE', 'Creatinine normal range', 'ACTIVE', 'G_PER_DL'),
    (7, 7.0, 20.0, now(), 'BUN_RANGE', 'Blood urea nitrogen range', 'ACTIVE', 'G_PER_DL'),
    (8, 120.0, 200.0, now(), 'CHOL_RANGE', 'Cholesterol range', 'ACTIVE', 'G_PER_DL'),
    (9, 8.0, 48.0, now(), 'AST_RANGE', 'Aspartate transaminase range', 'ACTIVE', 'G_PER_DL'),
    (10, 7.0, 55.0, now(), 'ALT_RANGE', 'Alanine transaminase range', 'ACTIVE', 'G_PER_DL'),
    (11, 0.0, 10.0, now(), 'CRP_RANGE', 'C-reactive protein threshold', 'ACTIVE', 'G_PER_DL'),
    (12, NULL, NULL, now(), 'SYSTEM_VERSION', 'System schema version', 'ACTIVE', NULL),
    (13, NULL, NULL, now(), 'SYNC_INTERVAL_MIN', 'Configuration sync interval', 'ACTIVE', 'MINUTES'),
    (14, 0.0, 100.0, now(), 'DEVICE_TEMP_LIMIT', 'Device temperature alert range', 'ACTIVE', 'CELSIUS'),
    (15, 0.0, 100.0, now(), 'CALIBRATION_PROGRESS', 'Calibration progress range', 'ACTIVE', 'PERCENTAGE');
