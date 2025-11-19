-- =========================================================
-- Flyway Migration: Seed initial data
-- Tables: parameters, reagent_used, synced_configurations
-- =========================================================

-- =========================================================
-- Table: parameters
-- =========================================================
-- INSERT INTO parameters
-- (created_at, updated_at, createdBy, updatedBy, name, param_code, abbreviation, description, min, max, status, unit, gender, price)
-- VALUES
-- -- White Blood Cell Count
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'White Blood Cell Count', 'WBC', 'WBC', 'Measures the number of white blood cells (leukocytes) in the blood, which helps fight infection.', 4000, 10000, 'ACTIVE', 'CELLS_PER_UL', 'BOTH', 10000),
-- -- Red Blood Cell Count - Male
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Red Blood Cell Count', 'RBC', 'RBC', 'Measures the number of red blood cells per unit of blood, which are responsible for carrying oxygen throughout the body.', 4.7, 6.1, 'ACTIVE', 'MILLIONS_PER_UL', 'MALE', 12000),
-- -- Red Blood Cell Count - Female
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Red Blood Cell Count', 'RBC', 'RBC', 'Measures the number of red blood cells per unit of blood, which are responsible for carrying oxygen throughout the body.', 4.2, 5.4, 'ACTIVE', 'MILLIONS_PER_UL', 'FEMALE', 11000),
-- -- Hemoglobin - Male
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Hemoglobin', 'HGB', 'Hb/HGB', 'Measures the amount of hemoglobin in the blood, the protein in red blood cells that carries oxygen.', 14, 18, 'ACTIVE', 'G_PER_DL', 'MALE', 15000),
-- -- Hemoglobin - Female
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Hemoglobin', 'HGB', 'Hb/HGB', 'Measures the amount of hemoglobin in the blood, the protein in red blood cells that carries oxygen.', 12, 16, 'ACTIVE', 'G_PER_DL', 'FEMALE', 14000),
-- -- Hematocrit - Male
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Hematocrit', 'HCT', 'HCT', 'Represents the percentage of red blood cells in the blood volume, indicating oxygen-carrying capacity.', 42, 52, 'ACTIVE', 'PERCENTAGE', 'MALE', 13000),
-- -- Hematocrit - Female
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Hematocrit', 'HCT', 'HCT', 'Represents the percentage of red blood cells in the blood volume, indicating oxygen-carrying capacity.', 37, 47, 'ACTIVE', 'PERCENTAGE', 'FEMALE', 12000),
-- -- Platelet Count
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Platelet Count', 'PLT', 'PLT', 'Measures the number of platelets in the blood, which are responsible for clotting.', 150000, 350000, 'ACTIVE', 'CELLS_PER_UL', 'BOTH', 9000),
-- -- Mean Corpuscular Volume
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Mean Corpuscular Volume', 'MCV', 'MCV', 'Indicates the average size of red blood cells.', 80, 100, 'ACTIVE', 'FL', 'BOTH', 8000),
-- -- Mean Corpuscular Haemoglobin
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Mean Corpuscular Haemoglobin', 'MCH', 'MCH', 'Represents the average amount of haemoglobin per red blood cell.', 27, 33, 'ACTIVE', 'PG', 'BOTH', 7500),
-- -- Mean Corpuscular Haemoglobin Concentration
-- (NOW(), NOW(), FLOOR(RANDOM() * 5 + 1011)::INT, FLOOR(RANDOM() * 5 + 1011)::INT, 'Mean Corpuscular Haemoglobin Concentration', 'MCHC', 'MCHC', 'Calculates the average concentration of haemoglobin in red blood cells.', 32, 36, 'ACTIVE', 'G_PER_DL', 'BOTH', 7000);

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
