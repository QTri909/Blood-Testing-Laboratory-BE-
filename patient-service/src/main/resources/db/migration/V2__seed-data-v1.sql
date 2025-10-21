-- ==============================
-- SEED DATA
-- ==============================

-- 1️⃣ User snapshots
INSERT INTO user_snapshot (
    id, external_user_id, last_updated
) VALUES
      (1, 101, CURRENT_TIMESTAMP),
      (2, 102, CURRENT_TIMESTAMP),
      (3, 103, CURRENT_TIMESTAMP),
      (4, 104, CURRENT_TIMESTAMP),
      (5, 105, CURRENT_TIMESTAMP),
      (6, 106, CURRENT_TIMESTAMP),
      (7, 107, CURRENT_TIMESTAMP),
      (8, 108, CURRENT_TIMESTAMP),
      (9, 109, CURRENT_TIMESTAMP),
      (10, 110, CURRENT_TIMESTAMP),
      (11, 111, CURRENT_TIMESTAMP);

-- 2️⃣ Medical records (with record_code)
INSERT INTO medical_record (
    record_id, patient_id, assigned_user, created_by, updated_by,
    created_at, updated_at, visit_date
) VALUES
      (1001, 102, 101, 101, 103, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (1002, 102, 101, 101, 103, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (1003, 104, 101, 101, 103, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (1004, 104, 103, 103, 101, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3️⃣ Clinical notes
INSERT INTO clinical_note (
    note_id, record_id, noted_by, note, created_at, updated_at
) VALUES
      (5001, 1001, 101, 'Patient is recovering well.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (5002, 1002, 101, 'Follow-up scheduled for next week.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (5003, 1003, 103, 'Patient showing improvement.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (5004, 1004, 103, 'Prescription updated.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
