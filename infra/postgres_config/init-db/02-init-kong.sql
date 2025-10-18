-- ==============================================================
-- Script khởi tạo database cho Kong
-- ==============================================================

-- User đã được tạo trong 00-setup-auth.sql
-- Chỉ cần tạo database và gán ownership

-- Tạo database cho Kong
CREATE DATABASE kong_db WITH OWNER kong_user;

-- Kết nối vào database vừa tạo
\c kong_db

-- Thu hồi tất cả quyền mặc định từ public
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON DATABASE kong_db FROM PUBLIC;

-- Cấp quyền cho kong_user
GRANT CONNECT ON DATABASE kong_db TO kong_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO kong_user;
GRANT ALL PRIVILEGES ON DATABASE kong_db TO kong_user;

-- Đặt default privileges để tự động cấp quyền cho các object mới
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO kong_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO kong_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO kong_user;

-- Log thông tin
\echo '✓ Kong database initialized successfully'
\echo '  - Database: kong_db'
\echo '  - User: kong_user'
\echo '  - Schema: public'