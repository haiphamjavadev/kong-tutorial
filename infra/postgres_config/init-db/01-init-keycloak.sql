-- ==============================================================
-- Script khởi tạo database cho Keycloak
-- ==============================================================

-- User đã được tạo trong 00-setup-auth.sql
-- Chỉ cần tạo database và gán ownership

-- Tạo database cho Keycloak
CREATE DATABASE keycloak_db WITH OWNER keycloak_user;

-- Kết nối vào database vừa tạo
\c keycloak_db

-- Thu hồi tất cả quyền mặc định từ public
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON DATABASE keycloak_db FROM PUBLIC;

-- Cấp quyền cho keycloak_user
GRANT CONNECT ON DATABASE keycloak_db TO keycloak_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak_user;
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO keycloak_user;

-- Đặt default privileges để tự động cấp quyền cho các object mới
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO keycloak_user;

-- Tạo schema auth nếu cần (cho custom user role provider)
CREATE SCHEMA IF NOT EXISTS auth AUTHORIZATION keycloak_user;
GRANT ALL PRIVILEGES ON SCHEMA auth TO keycloak_user;

-- Log thông tin
\echo '✓ Keycloak database initialized successfully'
\echo '  - Database: keycloak_db'
\echo '  - User: keycloak_user'
\echo '  - Schema: public, auth'