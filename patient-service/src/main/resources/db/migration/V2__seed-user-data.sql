-- V2__seed_user_snapshot.sql
-- Seed initial 15 user_snapshot entries for testing

INSERT INTO user_snapshot
(external_user_id, full_name, email, phone_number, identity_number, last_updated, roles)
VALUES
    (9, 'Trần Thị G', 'luivuton10@gmail.com', '+84913311345', '083434832343382', NOW(), '["PATIENT"]'::jsonb),
    (10, 'Trần Thị G', 'luivuton11@gmail.com', '+84913311345', '0834334832343382', NOW(), '["PATIENT"]'::jsonb),
    (11, 'Trần Thị G', 'luivuton13@gmail.com', '+84913311345', '08343344832343382', NOW(), '["PATIENT"]'::jsonb),
    (12, 'Trần Thị G', 'luivuton14@gmail.com', '+84913311345', '083432344832343382', NOW(), '["PATIENT"]'::jsonb),
    (28, 'Trần Thị T', 'luivuton19@gmail.com', '+84933311345', '0834312344832343382', NOW(), '["PATIENT"]'::jsonb),
    (29, 'Trần Thị T', 'luivuton29@gmail.com', '+84833311345', '08342344832343382', NOW(), '["PATIENT"]'::jsonb),
    (7, 'Trần Thị D', 'luivuton8@gmail.com', '+84912312345', '0834383435382', NOW(), '["PATIENT"]'::jsonb),
    (31, 'Lưu Vũ', 'monkay.d.minato@gmail.com', '+84833351345', '0834483243382', NOW(), '["PATIENT"]'::jsonb),
    (33, 'Trần Thị N', 'luivuton78@gmail.com', '+84935342345', '0830068435382', NOW(), '["PATIENT"]'::jsonb),
    (35, 'Hai CUong', 'hyhc9104@gmail.com', '+8480721113', 'VN15222118', NOW(), '["PATIENT"]'::jsonb),
    (24, 'Nguyen Van H', 'luivuton88@gmail.com', '+84935342345', '0830088435382', NOW(), '["PATIENT"]'::jsonb),
    (5, 'Trần Thịng A', 'imquocvu1@gmail.com', '+84912312345', '065422454455', NOW(), '["PATIENT"]'::jsonb),
    (3, 'Trần bm a', 'luivuton@gmail.com', '+84912312345', '33555551737253485612551', NOW(), '["PATIENT"]'::jsonb),
    (38, 'Lucke', 'huyacsp3@gmail.com', '+84439799374', '97549983940', NOW(), '["PATIENT"]'::jsonb),
    (4, 'Trần Thị A', 'luivuton1@gmail.com', '+84912312345', '083438347382', NOW(), '["PATIENT"]'::jsonb),
    (39, 'Ha Hai Cuong', 'Cuongcuong99@gmail.com', '+84848025119', '0234923984234', NOW(), '["PATIENT"]'::jsonb),
    (40, 'hjgjhghjg', 'hhc86778@gmail.com', '+84987765543', '678678687', NOW(), '["PATIENT"]'::jsonb),
    (1, 'System Admin', 'admin@system.com', '0900000000', '000000000', NOW(), '["ADMIN"]'::jsonb),
    (37, 'Hoàng Đặng', 'huyacsp1@gmail.com', '+84834229645', '0834348323433', NOW(), '["PATIENT"]'::jsonb),
    (34, 'Hai CUon 88g', 'hhc9104@gmail.com', '+8489721113', 'VN8008888', NOW(), '["PATIENT"]'::jsonb)