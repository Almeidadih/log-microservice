package com.logistica.avaliacao.config;

import com.logistica.avaliacao.domain.AvaliacaoId;
import com.logistica.avaliacao.domain.ColetaId;
import com.logistica.avaliacao.domain.OrigemAvaliacao;
import com.logistica.avaliacao.domain.TransportadoraId;
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
                new ColetaIdToUuid(), new UuidToColetaId(),
                new TransportadoraIdToUuid(), new UuidToTransportadoraId(),
                new AvaliacaoIdToUuid(), new UuidToAvaliacaoId(),
                new OrigemAvaliacaoToString(), new StringToOrigemAvaliacao()
        );
        return new R2dbcCustomConversions(R2dbcCustomConversions.STORE_CONVERSIONS, converters);
    }

    @WritingConverter
    static class OrigemAvaliacaoToString implements Converter<OrigemAvaliacao, String> {
        public String convert(OrigemAvaliacao source) { return source.name(); }
    }
    @ReadingConverter
    static class StringToOrigemAvaliacao implements Converter<String, OrigemAvaliacao> {
        public OrigemAvaliacao convert(String source) { return OrigemAvaliacao.valueOf(source); }
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
    static class TransportadoraIdToUuid implements Converter<TransportadoraId, UUID> {
        public UUID convert(TransportadoraId source) { return source.value(); }
    }
    @ReadingConverter
    static class UuidToTransportadoraId implements Converter<UUID, TransportadoraId> {
        public TransportadoraId convert(UUID source) { return new TransportadoraId(source); }
    }

    @WritingConverter
    static class AvaliacaoIdToUuid implements Converter<AvaliacaoId, UUID> {
        public UUID convert(AvaliacaoId source) { return source.value(); }
    }
    @ReadingConverter
    static class UuidToAvaliacaoId implements Converter<UUID, AvaliacaoId> {
        public AvaliacaoId convert(UUID source) { return new AvaliacaoId(source); }
    }
}
