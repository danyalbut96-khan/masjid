-- ✦ MASJID CLOUD - DATABASE SCHEMA FOR SUPABASE ✦
-- Copy and paste this script directly into Supabase SQL Editor to create your tables.

-- 0. Create Users Table
CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    masjid VARCHAR(255) NOT NULL
);

-- 1. Create Donors Table
CREATE TABLE IF NOT EXISTS donors (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    type VARCHAR(50),
    masjid VARCHAR(255) NOT NULL
);

-- 2. Create Donations Table
CREATE TABLE IF NOT EXISTS donations (
    id VARCHAR(50) PRIMARY KEY,
    donor_id VARCHAR(50) REFERENCES donors(id) ON DELETE SET NULL,
    donor_name VARCHAR(255),
    amount NUMERIC(10, 2) NOT NULL,
    date DATE NOT NULL,
    purpose VARCHAR(100),
    masjid VARCHAR(255) NOT NULL
);

-- 3. Create Announcements Table
CREATE TABLE IF NOT EXISTS announcements (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50),
    date DATE NOT NULL,
    description TEXT,
    masjid VARCHAR(255) NOT NULL
);

-- 4. Create Staff Table
CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100),
    phone VARCHAR(50),
    salary NUMERIC(10, 2) NOT NULL,
    masjid VARCHAR(255) NOT NULL
);

-- 5. Create Namaz/Prayer Timings Table
CREATE TABLE IF NOT EXISTS namaz (
    masjid VARCHAR(255) PRIMARY KEY,
    fajr VARCHAR(20) NOT NULL DEFAULT '04:45 AM',
    dhuhr VARCHAR(20) NOT NULL DEFAULT '01:15 PM',
    asr VARCHAR(20) NOT NULL DEFAULT '05:00 PM',
    maghrib VARCHAR(20) NOT NULL DEFAULT '07:05 PM',
    isha VARCHAR(20) NOT NULL DEFAULT '08:45 PM',
    jummah VARCHAR(20) NOT NULL DEFAULT '01:30 PM'
);

-- Optional: Enable Row Level Security (RLS) but allow anonymous access for simple testing
ALTER TABLE donors ENABLE ROW LEVEL SECURITY;
ALTER TABLE donations ENABLE ROW LEVEL SECURITY;
ALTER TABLE announcements ENABLE ROW LEVEL SECURITY;
ALTER TABLE staff ENABLE ROW LEVEL SECURITY;
ALTER TABLE namaz ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow public select" ON users FOR SELECT USING (true);
CREATE POLICY "Allow public insert" ON users FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update" ON users FOR UPDATE USING (true);
CREATE POLICY "Allow public delete" ON users FOR DELETE USING (true);

CREATE POLICY "Allow public select" ON donors FOR SELECT USING (true);
CREATE POLICY "Allow public insert" ON donors FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update" ON donors FOR UPDATE USING (true);
CREATE POLICY "Allow public delete" ON donors FOR DELETE USING (true);

CREATE POLICY "Allow public select" ON donations FOR SELECT USING (true);
CREATE POLICY "Allow public insert" ON donations FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update" ON donations FOR UPDATE USING (true);
CREATE POLICY "Allow public delete" ON donations FOR DELETE USING (true);

CREATE POLICY "Allow public select" ON announcements FOR SELECT USING (true);
CREATE POLICY "Allow public insert" ON announcements FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update" ON announcements FOR UPDATE USING (true);
CREATE POLICY "Allow public delete" ON announcements FOR DELETE USING (true);

CREATE POLICY "Allow public select" ON staff FOR SELECT USING (true);
CREATE POLICY "Allow public insert" ON staff FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update" ON staff FOR UPDATE USING (true);
CREATE POLICY "Allow public delete" ON staff FOR DELETE USING (true);

CREATE POLICY "Allow public select" ON namaz FOR SELECT USING (true);
CREATE POLICY "Allow public insert" ON namaz FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update" ON namaz FOR UPDATE USING (true);
CREATE POLICY "Allow public delete" ON namaz FOR DELETE USING (true);
