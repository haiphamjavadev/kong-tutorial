FROM kong:3.9.1-ubuntu

USER root
RUN apt-get update && apt-get install -y git ca-certificates && rm -rf /var/lib/apt/lists/*

# Vendor libs cho lua-resty-openidc
RUN set -eux; \
    mkdir -p /tmp/libs; cd /tmp/libs; \
    git clone --depth=1 https://github.com/zmartzone/lua-resty-openidc.git; \
    # SỬ DỤNG FORK TƯƠNG THÍCH OPENSSL 3.x
    git clone --depth=1 https://github.com/cdbattags/lua-resty-jwt.git; \
    git clone --depth=1 https://github.com/ledgetech/lua-resty-http.git; \
    mkdir -p /usr/local/share/lua/5.1/resty; \
    cp -r lua-resty-openidc/lib/resty/* /usr/local/share/lua/5.1/resty/; \
    cp -r lua-resty-jwt/lib/resty/*   /usr/local/share/lua/5.1/resty/; \
    cp -r lua-resty-http/lib/resty/*  /usr/local/share/lua/5.1/resty/; \
    rm -rf /tmp/libs

# Plugin kong-oidc
RUN set -eux; \
    git clone --depth=1 https://github.com/revomatico/kong-oidc.git /tmp/kong-oidc; \
    mkdir -p /usr/local/share/lua/5.1/kong/plugins/oidc; \
    cp -r /tmp/kong-oidc/kong/plugins/oidc/* /usr/local/share/lua/5.1/kong/plugins/oidc/; \
    rm -rf /tmp/kong-oidc

RUN mkdir -p /kong_config
COPY kong.yml /kong_config/kong.yml

USER kong