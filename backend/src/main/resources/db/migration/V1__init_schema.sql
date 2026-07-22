-- V1__init_schema.sql
-- Initial schema for the Hostel Management System (PostgreSQL)

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE staff (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    designation VARCHAR(100),
    phone VARCHAR(20),
    department VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(100),
    year INTEGER,
    guardian_name VARCHAR(255),
    guardian_phone VARCHAR(20),
    address VARCHAR(500),
    admission_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE hostels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    type VARCHAR(20),
    total_capacity INTEGER,
    warden_id BIGINT REFERENCES staff(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE blocks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    hostel_id BIGINT NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE floors (
    id BIGSERIAL PRIMARY KEY,
    floor_number INTEGER,
    block_id BIGINT NOT NULL REFERENCES blocks(id) ON DELETE CASCADE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    room_number VARCHAR(50) NOT NULL,
    floor_id BIGINT NOT NULL REFERENCES floors(id) ON DELETE CASCADE,
    capacity INTEGER,
    occupied INTEGER NOT NULL DEFAULT 0,
    room_type VARCHAR(20),
    rent_amount NUMERIC(10, 2),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE room_allocations (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id),
    room_id BIGINT NOT NULL REFERENCES rooms(id),
    allocated_date DATE,
    vacated_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id),
    date DATE NOT NULL,
    status VARCHAR(20),
    marked_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE (student_id, date)
);

CREATE TABLE leave_requests (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id),
    from_date DATE,
    to_date DATE,
    reason VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by BIGINT REFERENCES users(id),
    remarks VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE complaints (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    category VARCHAR(20),
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE complaint_replies (
    id BIGSERIAL PRIMARY KEY,
    complaint_id BIGINT NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    replied_by BIGINT NOT NULL REFERENCES users(id),
    message VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id),
    amount NUMERIC(10, 2) NOT NULL,
    payment_type VARCHAR(30),
    payment_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    payment_method VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE visitors (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id),
    visitor_name VARCHAR(255) NOT NULL,
    relation VARCHAR(100),
    phone VARCHAR(20),
    purpose VARCHAR(255),
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'CHECKED_IN',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE announcements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(3000),
    posted_by BIGINT NOT NULL REFERENCES users(id),
    target_audience VARCHAR(20) NOT NULL DEFAULT 'ALL',
    expiry_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000),
    type VARCHAR(20),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(255) NOT NULL,
    entity_name VARCHAR(100),
    entity_id BIGINT,
    details VARCHAR(2000),
    ip_address VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_students_roll_number ON students(roll_number);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_leave_requests_status ON leave_requests(status);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
