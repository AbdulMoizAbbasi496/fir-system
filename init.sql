CREATE TABLE IF NOT EXISTS fir_records (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    fir_number    VARCHAR(30)  NOT NULL UNIQUE,
    complainant   VARCHAR(100) NOT NULL,
    cnic          VARCHAR(20)  NOT NULL,
    contact       VARCHAR(20)  NOT NULL,
    crime_type    VARCHAR(50)  NOT NULL,
    location      VARCHAR(150) NOT NULL,
    incident_date DATE         NOT NULL,
    description   TEXT,
    status        VARCHAR(30)  DEFAULT 'Under Investigation',
    officer_name  VARCHAR(100) NOT NULL,
    registered_at DATETIME     DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO fir_records
    (fir_number, complainant, cnic, contact, crime_type,
     location, incident_date, description, officer_name, status)
VALUES
    ('FIR-2026-001', 'Ahmed Raza',    '37405-1111111-1', '0300-1111111',
     'Theft',     'Mall Road, Lahore',          '2026-04-10',
     'Mobile phone snatching near main gate',   'DSP Khalid Mehmood', 'Under Investigation'),

    ('FIR-2026-002', 'Fatima Noor',   '37405-2222222-2', '0301-2222222',
     'Fraud',     'Model Town, Lahore',          '2026-04-15',
     'Online banking fraud of Rs. 50,000',      'Inspector Asif Ali',  'Challan Submitted'),

    ('FIR-2026-003', 'Bilal Hussain', '37405-3333333-3', '0302-3333333',
     'Assault',   'Johar Town, Lahore',          '2026-04-20',
     'Physical assault outside a restaurant',   'DSP Khalid Mehmood', 'Court Proceedings'),

    ('FIR-2026-004', 'Sana Sheikh',   '37405-4444444-4', '0303-4444444',
     'Cybercrime', 'DHA Phase 5, Lahore',        '2026-04-22',
     'Harassment via social media accounts',    'Inspector Asif Ali',  'Under Investigation'),

    ('FIR-2026-005', 'Tariq Mahmood', '37405-5555555-5', '0304-5555555',
     'Robbery',   'Gulberg III, Lahore',         '2026-04-25',
     'Armed robbery at a jewellery shop',       'DSP Khalid Mehmood', 'Under Investigation');
