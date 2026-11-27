package com.gamelive.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI 元数据。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gameLiveOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GameLive API")
                        .version("1.0.0")
                        .description("GameLive 游戏直播平台后端接口"));
    }
}
