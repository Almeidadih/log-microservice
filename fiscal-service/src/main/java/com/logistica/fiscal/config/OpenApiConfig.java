package com.logistica.fiscal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A UI fica em /swagger-ui.html e o JSON da especificação em
 * /v3/api-docs — o springdoc gera os dois automaticamente a partir dos
 * controllers, sem precisar anotar cada endpoint manualmente (embora
 * anotações extras em cima disso, tipo @Operation, deixem a documentação
 * mais rica quando fizer sentido).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fiscal Service")
                        .description("Ciclo de vida das notas fiscais: registro, conferência, devolução e "
                                + "cancelamento, com publicação de eventos via outbox transacional para o Kafka.")
                        .version("v1")
                        .contact(new Contact().name("Diego")));
    }
}
