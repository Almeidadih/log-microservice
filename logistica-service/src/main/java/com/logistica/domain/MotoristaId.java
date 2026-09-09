package com.logistica.domain;

import java.util.UUID;

public record MotoristaId(UUID value) {

    public static MotoristaId novo(){
        return new MotoristaId(UUID.randomUUID());
    }
    public static MotoristaId de(String texto){
        return new MotoristaId(UUID.fromString(texto));
    }

    @Override
    public String toString() {
        return value.toString() ;
    }
}
