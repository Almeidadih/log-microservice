package com.logistica.fiscal.config;

import com.logistica.fiscal.domain.CustomerId;
import com.logistica.fiscal.domain.NotaFiscalId;
import com.logistica.fiscal.domain.StatusNotaFiscal;
import com.logistica.fiscal.domain.TransportadoraId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.util.List;
import java.util.UUID;


/**
 * Registra os conversores necessários para o R2DBC entender os IDs tipados
 * (NotaFiscalId, CustomerId, TransportadoraId) como colunas UUID no Postgres.
 */
@Configuration
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Object> converters = List.of(
                new NotaFiscalIdToUuid(), new UuidToNotaFiscalId(),
                new CustomerIdToUuid(), new UuidToCustomerId(),
                new TransportadoraIdToUuid(), new UuidToTransportadoraId(),
                new StatusToString(), new StringToStatus()
        );
        return new R2dbcCustomConversions(R2dbcCustomConversions.STORE_CONVERSIONS, converters);
    }

    @WritingConverter
    static class NotaFiscalIdToUuid implements Converter<NotaFiscalId, UUID> {
        public UUID convert(NotaFiscalId source)
        { return source.value(); }
    }

    @ReadingConverter
    static class UuidToNotaFiscalId implements Converter<UUID, NotaFiscalId> {
        public NotaFiscalId convert(UUID source) { return new NotaFiscalId(source); }
    }

    @WritingConverter
    static class CustomerIdToUuid implements Converter<CustomerId, UUID> {
        public UUID convert(CustomerId source) { return source.value(); }
    }

    @ReadingConverter
    static class UuidToCustomerId implements Converter<UUID, CustomerId> {
        public CustomerId convert(UUID source) { return new CustomerId(source); }
    }

    @WritingConverter
    static class TransportadoraIdToUuid implements Converter<TransportadoraId, UUID> {
        public UUID convert(TransportadoraId source) { return source.value(); }
    }

    @ReadingConverter
    static class UuidToTransportadoraId implements Converter<UUID, TransportadoraId> {
        public TransportadoraId convert(UUID source) { return new TransportadoraId(source); }
    }

    @WritingConverter
    static class StatusToString implements Converter<StatusNotaFiscal, String> {
        public String convert(StatusNotaFiscal source) { return source.name(); }
    }

    @ReadingConverter
    static class StringToStatus implements Converter<String, StatusNotaFiscal> {
        public StatusNotaFiscal convert(String source) { return StatusNotaFiscal.valueOf(source); }
    }
}
