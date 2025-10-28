-- V2__seed_user_snapshot.sql
-- Seed initial 15 user_snapshot entries for testing

INSERT INTO user_snapshot (external_user_id, last_updated, roles)
VALUES
    (1001, TIMESTAMP '2025-01-10 09:15:00', '["PATIENT"]'::jsonb),
    (1002, TIMESTAMP '2025-01-11 10:45:00', '["PATIENT"]'::jsonb),
    (1003, TIMESTAMP '2025-01-12 08:20:00', '["PATIENT"]'::jsonb),
    (1004, TIMESTAMP '2025-01-13 14:35:00', '["PATIENT"]'::jsonb),
    (1005, TIMESTAMP '2025-01-14 17:55:00', '["PATIENT"]'::jsonb),
    (1006, TIMESTAMP '2025-01-15 12:10:00', '["DOCTOR"]'::jsonb),
    (1007, TIMESTAMP '2025-01-16 09:50:00', '["DOCTOR"]'::jsonb),
    (1008, TIMESTAMP '2025-01-17 11:40:00', '["DOCTOR"]'::jsonb),
    (1009, TIMESTAMP '2025-01-18 15:25:00', '["DOCTOR"]'::jsonb),
    (1010, TIMESTAMP '2025-01-19 16:05:00', '["DOCTOR"]'::jsonb),
    (1011, TIMESTAMP '2025-01-20 08:45:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb),
    (1012, TIMESTAMP '2025-01-21 13:30:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb),
    (1013, TIMESTAMP '2025-01-22 07:55:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb),
    (1014, TIMESTAMP '2025-01-23 10:10:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb),
    (1015, TIMESTAMP '2025-01-24 09:00:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb);
