-- ==============================
-- SEED DATA
-- ==============================

INSERT INTO user_snapshot (
    id, external_user_id, date_of_birth, last_updated, gender,
    phone_number, first_name, last_name, email, username, address, role, status
) VALUES
      (1, 101, '1980-01-01', CURRENT_TIMESTAMP, 'MALE', '1234567890', 'John', 'Doe', 'john.doe@example.com', 'johndoe', '123 Main St', 'ROLE_DOCTOR', 'ACTIVE'),
      (2, 102, '1990-02-02', CURRENT_TIMESTAMP, 'FEMALE', '0987654321', 'Jane', 'Smith', 'jane.smith@example.com', 'janesmith', '456 Oak St', 'ROLE_PATIENT', 'ACTIVE'),
      (3, 103, '1975-03-03', CURRENT_TIMESTAMP, 'MALE', '5551234567', 'Admin', 'User', 'admin@example.com', 'adminuser', '789 Pine St', 'ROLE_ADMIN', 'ACTIVE');

INSERT INTO medical_record (
    record_id, patient_id, assigned_user, created_by, updated_by,
    created_at, updated_at, visit_date
) VALUES
      (1001, 102, 101, 101, 103, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (1002, 102, 101, 101, 103, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO clinical_note (
    note_id, record_id, noted_by, note, created_at, updated_at
) VALUES
      (5001, 1001, 101, 'Patient is recovering well.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (5002, 1002, 101, 'Follow-up scheduled for next week.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
