package com.logistica.cepservice.config;

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
                        .title("cep-service")
                        .description("Consulta de CEPs com cache em Redis (cache-aside) e cálculo aproximado de "
                                + "distância entre dois pontos. Inclui um endpoint de importação em lote da base "
                                + "de CEPs, paralelizado com Virtual Threads.")
                        .version("v1")
                        .contact(new Contact().name("Diego")));
    }
}
