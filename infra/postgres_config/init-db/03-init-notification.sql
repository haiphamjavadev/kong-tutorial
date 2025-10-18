-- ==============================================================
-- Script to initialize the database for Notification
-- ==============================================================

-- The user has been created in 00-setup-auth.sql
-- Only need to create the database and assign ownership

-- Create database for Notification
CREATE DATABASE notification_db WITH OWNER notification_user;

-- Connect to the newly created database
\c notification_db

-- Revoke all default permissions from public
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON DATABASE notification_db FROM PUBLIC;

-- Grant permissions to notification_user
GRANT CONNECT ON DATABASE notification_db TO notification_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO notification_user;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO notification_user;

-- Set default privileges to automatically grant permissions for new objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO notification_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO notification_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO notification_user;

-- Log information
\echo '✓ Notification database initialized successfully'
\echo '  - Database: notification_db'
\echo '  - User: notification_user'
\echo '  - Schema: public'