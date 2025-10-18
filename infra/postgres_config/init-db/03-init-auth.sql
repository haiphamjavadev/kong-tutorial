-- ==============================================================
-- Script khởi tạo database cho Auth
-- ==============================================================

-- User đã được tạo trong 00-setup-auth.sql
-- Chỉ cần tạo database và gán ownership

-- Tạo database cho Auth
CREATE DATABASE auth_db WITH OWNER auth_user;

-- Kết nối vào database vừa tạo
\c auth_db

-- Thu hồi tất cả quyền mặc định từ public
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON DATABASE auth_db FROM PUBLIC;

-- Cấp quyền cho auth_user
GRANT CONNECT ON DATABASE auth_db TO auth_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;

-- Đặt default privileges để tự động cấp quyền cho các object mới
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO auth_user;

-- Log thông tin
\echo '✓ Auth database initialized successfully'
\echo '  - Database: auth_db'
\echo '  - User: auth_user'
\echo '  - Schema: public'