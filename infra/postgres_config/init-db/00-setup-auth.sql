-- ==============================================================
-- Script cấu hình xác thực cho PostgreSQL
-- File này chạy đầu tiên (00-) để setup authentication
-- ==============================================================

-- Tạo role cho Kong và Keycloak trước
CREATE ROLE kong_user WITH LOGIN PASSWORD 'kong_secure_password' CREATEDB;
CREATE ROLE keycloak_user WITH LOGIN PASSWORD 'keycloak_secure_password';

-- Tạo role cho app
CREATE ROLE auth_user WITH LOGIN PASSWORD 'auth_secure_password';
CREATE ROLE notification_user WITH LOGIN PASSWORD 'notification_secure_password';

-- Log thông tin
\echo '✓ Authentication roles created successfully'
\echo '  - kong_user: ready for kong_db'
\echo '  - keycloak_user: ready for keycloak_db'