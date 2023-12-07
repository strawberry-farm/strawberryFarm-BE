package com.strawberryfarm.fitingle.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // HTTPS
        Server httpsServer = new Server();
        httpsServer.setUrl("https://strawberryfarm.shop/");

        // HTTP
        Server httpServer = new Server();
        httpServer.setUrl("http://strawberryfarm.shop/");

        return new OpenAPI()
            .components(new Components())
            .info(apiInfo())
            .servers(List.of(httpsServer, httpServer));
    }

    private Info apiInfo() {
        return new Info()
            .title("Fitingle API")
            .description("Fitingle API")
            .version("1.0.0");
    }
}
