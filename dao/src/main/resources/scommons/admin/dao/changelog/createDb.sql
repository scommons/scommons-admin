
-- Create db admin user
CREATE ROLE admin_admin WITH LOGIN PASSWORD 'superadmin';

-- Create app user
CREATE ROLE admin WITH LOGIN PASSWORD 'admin';

-- Creating admin_db database
CREATE DATABASE admin_db OWNER admin_admin;

