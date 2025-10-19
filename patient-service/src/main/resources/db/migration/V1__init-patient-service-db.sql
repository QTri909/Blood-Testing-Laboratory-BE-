-- ==============================
-- 0. Ensure the pgcrypto extension exists
-- ==============================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ==============================
-- 1. Create user_snapshot
-- ==============================
CREATE TABLE user_snapshot (
                               date_of_birth date NOT NULL,
                               external_user_id bigint NOT NULL,
                               id bigint NOT NULL,
                               last_updated timestamp(6) NOT NULL,
                               gender varchar(10) NOT NULL CHECK (gender IN ('MALE','FEMALE')),
                               phone_number varchar(15),
                               first_name varchar(50),
                               last_name varchar(50),
                               email varchar(100),
                               username varchar(100) NOT NULL,
                               address varchar(255) NOT NULL,
                               role varchar(255) NOT NULL CHECK (role IN ('ROLE_DOCTOR','ROLE_PATIENT','ROLE_ADMIN','ROLE_SUPERVISOR')),
                               status varchar(255) CHECK (status IN ('ACTIVE','INACTIVE','DELETED')),
                               PRIMARY KEY (id),
                               UNIQUE (external_user_id)
);

-- ==============================
-- 2. Create medical_record (with record_code)
-- ==============================
CREATE TABLE medical_record (
                                assigned_user bigint,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                created_by bigint NOT NULL,
                                patient_id bigint NOT NULL,
                                record_id bigint NOT NULL,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                updated_by bigint,
                                visit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                record_code UUID DEFAULT gen_random_uuid() NOT NULL,
                                PRIMARY KEY (record_id),
                                UNIQUE (record_code)
);

-- ==============================
-- 3. Create clinical_note
-- ==============================
CREATE TABLE clinical_note (
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               note_id bigint NOT NULL,
                               noted_by bigint NOT NULL,
                               record_id bigint NOT NULL,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               note TEXT NOT NULL,
                               PRIMARY KEY (note_id)
);

-- ==============================
-- 4. Create indexes
-- ==============================
CREATE INDEX clinical_note_record_id_index
    ON clinical_note (record_id);

CREATE INDEX medical_record_patient_id_index
    ON medical_record (patient_id);

-- ==============================
-- 5. Add foreign key constraints
-- ==============================
ALTER TABLE clinical_note
    ADD CONSTRAINT FKqmpb8j7eu4d0f3xbnagi1tpr1
        FOREIGN KEY (record_id)
            REFERENCES medical_record;

ALTER TABLE clinical_note
    ADD CONSTRAINT FKh9274baugyjam6prjh33pg9ww
        FOREIGN KEY (noted_by)
            REFERENCES user_snapshot (external_user_id);

ALTER TABLE medical_record
    ADD CONSTRAINT FKr5ql21ipdvf8awi96v8pyauxw
        FOREIGN KEY (assigned_user)
            REFERENCES user_snapshot (external_user_id);

ALTER TABLE medical_record
    ADD CONSTRAINT FKti9m2r7ukl7kqm4xtdpow6nhn
        FOREIGN KEY (created_by)
            REFERENCES user_snapshot (external_user_id);

ALTER TABLE medical_record
    ADD CONSTRAINT FKlcvw5svskgwm39di45i4ivp0g
        FOREIGN KEY (patient_id)
            REFERENCES user_snapshot (external_user_id);

ALTER TABLE medical_record
    ADD CONSTRAINT FKk17f3n5jw229oif3x314qtl5k
        FOREIGN KEY (updated_by)
            REFERENCES user_snapshot (external_user_id);
