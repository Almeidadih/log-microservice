package com.logistica.config;

import com.logistica.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;


import java.util.List;
import java.util.UUID;

@Configuration
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Object> converters = List.of(
                new TransportadoraIdToUuid(), new UuidToTransportadoraId(),
                new MotoristaIdToUuid(), new UuidToMotoristaId(),
                new CaminhaoIdToUuid(), new UuidToCaminhaoId(),
                new ColetaIdToUuid(), new UuidToColetaId(),
                new NotaFiscalIdToUuid(), new UuidToNotaFiscalId(),
                new StatusColetaToString(), new StringToStatusColeta()
        );
        return new R2dbcCustomConversions(R2dbcCustomConversions.STORE_CONVERSIONS, converters);
    }

    @WritingConverter
    static class StatusColetaToString implements Converter<StatusColeta, String> {
        public String convert(StatusColeta source) { return source.name(); }
    }
    @ReadingConverter
    static class StringToStatusColeta implements Converter<String, StatusColeta> {
        public StatusColeta convert(String source) { return StatusColeta.valueOf(source); }
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
    static class MotoristaIdToUuid implements Converter<MotoristaId, UUID> {
        public UUID convert(MotoristaId source) { return source.value(); }
    }
    @ReadingConverter
    static class UuidToMotoristaId implements Converter<UUID, MotoristaId> {
        public MotoristaId convert(UUID source) { return new MotoristaId(source); }
    }

    @WritingConverter
    static class CaminhaoIdToUuid implements Converter<CaminhaoId, UUID> {
        public UUID convert(CaminhaoId source) { return source.value(); }
    }
    @ReadingConverter
    static class UuidToCaminhaoId implements Converter<UUID, CaminhaoId> {
        public CaminhaoId convert(UUID source) { return new CaminhaoId(source); }
    }

    @WritingConverter
    static class ColetaIdToUuid implements Converter<ColetaId, UUID> {
        public UUID convert(ColetaId source) { return source.value(); }
    }
    @ReadingConverter
    static class UuidToColetaId implements Converter<UUID, ColetaId> {
        public ColetaId convert(UUID source) { return new ColetaId(source); }
    }

    @WritingConverter
    static class NotaFiscalIdToUuid implements Converter<NotaFiscalId, UUID> {
        public UUID convert(NotaFiscalId source) { return source.value(); }
    }
    @ReadingConverter
    static class UuidToNotaFiscalId implements Converter<UUID, NotaFiscalId> {
        public NotaFiscalId convert(UUID source) { return new NotaFiscalId(source); }
    }
}

