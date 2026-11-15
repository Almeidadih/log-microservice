package com.logistica.avaliacao.config;

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
                        .title("avaliacao-service")
                        .description("Avaliações de qualidade e agilidade (1 a 5) por transportadora e região, "
                                + "manuais (via API) ou automáticas (calculadas a partir do evento de entrega "
                                + "concluída). Ranking agregado por transportadora/região.")
                        .version("v1")
                        .contact(new Contact().name("Diego")));
    }
}
