#!/bin/bash

# Tạo cấu trúc thư mục
echo "Đang tạo thư mục cấu hình..."
mkdir -p apisix_config/apisix
mkdir -p apisix_config/dashboard
mkdir -p logs

# Tạo file config.yaml cho APISIX
cat > apisix_config/apisix/config.yaml << 'EOF'
apisix:
  node_listen: 9080
  enable_ipv6: false

  enable_control: true
  control:
    ip: "0.0.0.0"
    port: 9092

deployment:
  admin:
    allow_admin:
      - 0.0.0.0/0
    admin_key:
      - name: "admin"
        key: edd1c9f034335f136f87ad84b625c8f1
        role: admin
      - name: "viewer"
        key: 4054f7cf07e344346cd3f287985e76a2
        role: viewer

  etcd:
    host:
      - "http://etcd:2379"
    prefix: "/apisix"
    timeout: 30

nginx_config:
  error_log: "/usr/local/apisix/logs/error.log"
  error_log_level: "warn"

  http:
    access_log: "/usr/local/apisix/logs/access.log"
    access_log_format: "$remote_addr - $remote_user [$time_local] $http_host \"$request\" $status $body_bytes_sent $request_time \"$http_referer\" \"$http_user_agent\" $upstream_addr $upstream_status $upstream_response_time \"$upstream_scheme://$upstream_host$upstream_uri\""

plugins:
  - real-ip
  - client-control
  - proxy-control
  - request-id
  - zipkin
  - ext-plugin-pre-req
  - fault-injection
  - mocking
  - serverless-pre-function
  - cors
  - ip-restriction
  - ua-restriction
  - referer-restriction
  - csrf
  - uri-blocker
  - request-validation
  - openid-connect
  - authz-casbin
  - authz-casdoor
  - wolf-rbac
  - ldap-auth
  - hmac-auth
  - basic-auth
  - jwt-auth
  - key-auth
  - consumer-restriction
  - forward-auth
  - opa
  - authz-keycloak
  - proxy-cache
  - body-transformer
  - proxy-mirror
  - proxy-rewrite
  - workflow
  - api-breaker
  - limit-conn
  - limit-count
  - limit-req
  - gzip
  - server-info
  - traffic-split
  - redirect
  - response-rewrite
  - degraphql
  - kafka-proxy
  - grpc-transcode
  - grpc-web
  - http-logger
  - skywalking-logger
  - google-cloud-logging
  - splunk-hec-logging
  - file-logger
  - syslog
  - tcp-logger
  - kafka-logger
  - rocketmq-logger
  - udp-logger
  - clickhouse-logger
  - tencent-cloud-cls
  - loggly
  - elasticsearch-logger
  - prometheus
  - datadog
  - loki-logger
  - node-status
  - opentelemetry
  - openwhisk
  - serverless-post-function
  - ext-plugin-post-req
  - azure-functions

plugin_attr:
  prometheus:
    export_addr:
      ip: "0.0.0.0"
      port: 9091
EOF

# Tạo file conf.yaml cho Dashboard
cat > apisix_config/dashboard/conf.yaml << 'EOF'
conf:
  listen:
    host: 0.0.0.0
    port: 9000

  allow_list:
    - 0.0.0.0/0

  etcd:
    endpoints:
      - etcd:2379
    prefix: /apisix
    mtls:
      key_file: ""
      cert_file: ""
      ca_file: ""

  log:
    error_log:
      level: warn
      file_path: /usr/local/apisix-dashboard/logs/error.log
    access_log:
      file_path: /usr/local/apisix-dashboard/logs/access.log

authentication:
  secret: secret
  expire_time: 3600
  users:
    - username: admin
      password: admin
    - username: user
      password: user

plugins:
  - api-breaker
  - authz-casbin
  - authz-casdoor
  - authz-keycloak
  - azure-functions
  - basic-auth
  - batch-requests
  - body-transformer
  - client-control
  - clickhouse-logger
  - consumer-restriction
  - cors
  - csrf
  - datadog
  - degraphql
  - echo
  - elasticsearch-logger
  - error-log-logger
  - ext-plugin-post-req
  - ext-plugin-post-resp
  - ext-plugin-pre-req
  - fault-injection
  - file-logger
  - forward-auth
  - google-cloud-logging
  - grpc-transcode
  - grpc-web
  - gzip
  - hmac-auth
  - http-logger
  - ip-restriction
  - jwt-auth
  - kafka-logger
  - kafka-proxy
  - key-auth
  - ldap-auth
  - limit-conn
  - limit-count
  - limit-req
  - loggly
  - loki-logger
  - mocking
  - node-status
  - opa
  - openid-connect
  - opentelemetry
  - openwhisk
  - prometheus
  - proxy-cache
  - proxy-control
  - proxy-mirror
  - proxy-rewrite
  - real-ip
  - redirect
  - referer-restriction
  - request-id
  - request-validation
  - response-rewrite
  - rocketmq-logger
  - server-info
  - serverless-post-function
  - serverless-pre-function
  - skywalking
  - skywalking-logger
  - splunk-hec-logging
  - syslog
  - tcp-logger
  - tencent-cloud-cls
  - traffic-split
  - ua-restriction
  - udp-logger
  - uri-blocker
  - wolf-rbac
  - workflow
  - zipkin
EOF

echo "Cấu hình đã được tạo thành công!"
echo ""
echo "Để khởi động APISIX, chạy:"
echo "  docker-compose up -d"
echo ""
echo "Truy cập Dashboard tại: http://localhost:9000"
echo "Tài khoản: admin / admin"
echo ""
echo "APISIX Admin API: http://localhost:9080"
echo "API Key: edd1c9f034335f136f87ad84b625c8f1"
echo ""
echo "Prometheus Metrics: http://localhost:9091/apisix/prometheus/metrics"