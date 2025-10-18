package com.haiph.kongtutorial.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class CustomSwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";


    @Value("${config.swagger.url:null}")
    private String lstServersTest;
    @Value("${config.swagger.title:null}")
    private String lstServersTestTitle;
    @Value("${config.swagger.project-name:API}")
    private String projectName;

    @Bean(name = "customOpenAPITest")
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(projectName)
                        .description(projectName)
                        .version("1.0.0")
                        .license(apiLicence()))
                .servers(serversList())
                .security(Collections.singletonList(new SecurityRequirement().addList(AUTHORIZATION_HEADER)));
    }

    private List<Server> serversList() {
        List<Server> servers = new ArrayList<>();
        if (lstServersTest != null) {
            String[] urls = lstServersTest.split(",");
            String[] titles = lstServersTestTitle.split(",");
            if (urls.length != titles.length) {
                return servers;
            }
            for (int i = 0; i < urls.length; i++) {
                servers.add(new Server().url(urls[i]).description(titles[i]));
            }
        }
        return servers;
    }

    private License apiLicence() {
        return new License()
                .name("Licensed By Pham Hai");
    }
}

