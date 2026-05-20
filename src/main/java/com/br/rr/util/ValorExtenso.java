package com.br.rr.util;

import java.math.BigDecimal;

public final class ValorExtenso {

    private static final String[] UNIDADES = {
        "", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove",
        "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete",
        "dezoito", "dezenove"
    };
    private static final String[] DEZENAS = {
        "", "", "vinte", "trinta", "quarenta", "cinquenta",
        "sessenta", "setenta", "oitenta", "noventa"
    };
    private static final String[] CENTENAS = {
        "", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos",
        "seiscentos", "setecentos", "oitocentos", "novecentos"
    };

    private ValorExtenso() {}

    public static String converter(BigDecimal valor) {
        if (valor == null || valor.signum() < 0) return "";
        long centavos = valor.multiply(BigDecimal.valueOf(100)).setScale(0, java.math.RoundingMode.HALF_UP).longValue();
        long reais = centavos / 100;
        long cents = centavos % 100;

        StringBuilder sb = new StringBuilder();
        if (reais == 0 && cents == 0) {
            return "zero reais";
        }
        if (reais > 0) {
            sb.append(porExtenso(reais));
            sb.append(reais == 1 ? " real" : " reais");
        }
        if (reais > 0 && cents > 0) {
            sb.append(" e ");
        }
        if (cents > 0) {
            sb.append(porExtenso(cents));
            sb.append(cents == 1 ? " centavo" : " centavos");
        }
        return sb.toString();
    }

    private static String porExtenso(long n) {
        if (n == 0) return "";
        if (n == 100) return "cem";
        if (n < 20) return UNIDADES[(int) n];
        if (n < 100) {
            String dez = DEZENAS[(int) (n / 10)];
            int resto = (int) (n % 10);
            return resto == 0 ? dez : dez + " e " + UNIDADES[resto];
        }
        if (n < 1000) {
            String cent = CENTENAS[(int) (n / 100)];
            long resto = n % 100;
            return resto == 0 ? cent : cent + " e " + porExtenso(resto);
        }
        if (n < 1_000_000) {
            long mil = n / 1000;
            long resto = n % 1000;
            String prefixo = mil == 1 ? "mil" : porExtenso(mil) + " mil";
            return resto == 0 ? prefixo : prefixo + " e " + porExtenso(resto);
        }
        long mi = n / 1_000_000;
        long resto = n % 1_000_000;
        String prefixo = mi == 1 ? "um milhão" : porExtenso(mi) + " milhões";
        return resto == 0 ? prefixo : prefixo + " e " + porExtenso(resto);
    }
}
