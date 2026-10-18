package com.logistica.cepservice.domain;

import com.logistica.cepservice.exception.CepInvalidoException;

public record Cep(String digitos) {

    /**
     * Value object para o CEP. Guarda sempre só os 8 dígitos (sem hífen),
     * validando o formato na criação — assim, uma vez que você tem um objeto
     * Cep em mãos, sabe que ele é válido; não precisa revalidar em cada método
     * que o recebe.
     */
    public Cep {
        if (digitos == null) {
            throw  new CepInvalidoException("null");
        }
        String limpo = digitos.replaceAll("[^0-9]", "");
        if (limpo.length() != 8) {
            throw new CepInvalidoException(digitos);
        }
        digitos = limpo;

    }
    /** Formato "01310-100", útil só para exibição. */
    public static Cep de(String texto){
        return new Cep(texto);
    }

    public String formatado(){
        return digitos.substring(0,5) + "-" + digitos.substring(5);
    }

    @Override
    public String toString() {
        return digitos;
    }
}
