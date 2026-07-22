-- Sample seed data for local development / demos
-- Note: the application also auto-seeds roles + a default admin user on startup (see DataSeeder.java)

INSERT INTO roles (name, created_at, updated_at) VALUES
    ('ADMIN', now(), now()),
    ('WARDEN', now(), now()),
    ('STAFF', now(), now()),
    ('STUDENT', now(), now())
ON CONFLICT (name) DO NOTHING;

-- Example hostel / block / floor / room hierarchy
INSERT INTO hostels (name, address, type, total_capacity, created_at, updated_at)
VALUES ('Sunrise Hostel', '12 College Road', 'BOYS', 200, now(), now());

INSERT INTO blocks (name, hostel_id, created_at, updated_at)
VALUES ('Block A', (SELECT id FROM hostels WHERE name = 'Sunrise Hostel'), now(), now());

INSERT INTO floors (floor_number, block_id, created_at, updated_at)
VALUES (1, (SELECT id FROM blocks WHERE name = 'Block A'), now(), now());

INSERT INTO rooms (room_number, floor_id, capacity, occupied, room_type, rent_amount, status, created_at, updated_at)
VALUES
    ('A101', (SELECT id FROM floors WHERE floor_number = 1), 2, 0, 'DOUBLE', 8000.00, 'AVAILABLE', now(), now()),
    ('A102', (SELECT id FROM floors WHERE floor_number = 1), 1, 0, 'SINGLE', 12000.00, 'AVAILABLE', now(), now());
