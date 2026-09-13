package com.logistica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("logistica-service")
                        .description("Transportadoras, motoristas, caminhões, coletas e entregas. Consome eventos "
                                + "de nota fiscal do Kafka e consulta o cep-service via HTTP para resolver a região "
                                + "de cada coleta.")
                        .version("v1")
                        .contact(new Contact().name("Diego")));
    }
}
