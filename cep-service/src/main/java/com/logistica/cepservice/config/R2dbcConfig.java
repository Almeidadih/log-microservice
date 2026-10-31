package com.logistica.cepservice.config;

import com.logistica.cepservice.domain.Cep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.util.List;

@Configuration
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Object> converters = List.of(new CepToString(), new StringToCep());
        return new R2dbcCustomConversions(R2dbcCustomConversions.STORE_CONVERSIONS, converters);
    }

    @WritingConverter
    static class CepToString implements Converter<Cep, String> {
        public String convert(Cep source) { return source.digitos(); }
    }

    @ReadingConverter
    static class StringToCep implements Converter<String, Cep> {
        public Cep convert(String source) { return new Cep(source); }
    }
}
