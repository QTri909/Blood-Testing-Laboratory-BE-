-- V2__seed_user_snapshot.sql
-- Seed initial 15 user_snapshot entries for testing

INSERT INTO user_snapshot (external_user_id, full_name, email, phone_number, last_updated, roles)
VALUES
    (1001, 'Nguyễn Văn An', 'nguyenvanan@example.com', '0901122331', TIMESTAMP '2025-01-10 09:15:00', '["PATIENT"]'::jsonb),
    (1002, 'Trần Thị Bích', 'tranthibich@example.com', '0901234562', TIMESTAMP '2025-01-11 10:45:00', '["PATIENT"]'::jsonb),
    (1003, 'Lê Quốc Huy', 'lequochuy@example.com', '0903344557', TIMESTAMP '2025-01-12 08:20:00', '["PATIENT"]'::jsonb),
    (1004, 'Phạm Thị Hồng', 'phamthihong@example.com', '0904566788', TIMESTAMP '2025-01-13 14:35:00', '["PATIENT"]'::jsonb),
    (1005, 'Đỗ Minh Tuấn', 'dominhtuan@example.com', '0905678990', TIMESTAMP '2025-01-14 17:55:00', '["PATIENT"]'::jsonb),
    (1006, 'Ngô Thanh Bình', 'ngothanhbinh@example.com', '0916788899', TIMESTAMP '2025-01-15 12:10:00', '["DOCTOR"]'::jsonb),
    (1007, 'Vũ Thị Lan', 'vuthilan@example.com', '0917889900', TIMESTAMP '2025-01-16 09:50:00', '["DOCTOR"]'::jsonb),
    (1008, 'Hoàng Văn Cường', 'hoangvancuong@example.com', '0918990011', TIMESTAMP '2025-01-17 11:40:00', '["DOCTOR"]'::jsonb),
    (1009, 'Nguyễn Thị Thu', 'nguyenthithu@example.com', '0919001122', TIMESTAMP '2025-01-18 15:25:00', '["DOCTOR"]'::jsonb),
    (1010, 'Trương Hữu Phát', 'truonghuuphat@example.com', '0920112334', TIMESTAMP '2025-01-19 16:05:00', '["DOCTOR"]'::jsonb),
    (1011, 'Lâm Thị Ngọc', 'lamthingoc@example.com', '0921234455', TIMESTAMP '2025-01-20 08:45:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb),
    (1012, 'Phan Văn Dũng', 'phanvandung@example.com', '0922345566', TIMESTAMP '2025-01-21 13:30:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb),
    (1013, 'Nguyễn Hữu Tài', 'nguyenhuutai@example.com', '0923456677', TIMESTAMP '2025-01-22 07:55:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb),
    (1014, 'Lê Thị Mai', 'lethimai@example.com', '0934567788', TIMESTAMP '2025-01-23 10:10:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb),
    (1015, 'Đặng Văn Long', 'dangvanlong@example.com', '0935678899', TIMESTAMP '2025-01-24 09:00:00', '["ADMIN", "LAB_SUPERVISOR"]'::jsonb);
