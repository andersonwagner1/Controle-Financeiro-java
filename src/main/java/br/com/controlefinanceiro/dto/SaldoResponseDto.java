package br.com.controlefinanceiro.dto;
import lombok.Builder;

import java.math.BigDecimal;


@Builder  
public record SaldoResponseDto(BigDecimal saldoInicial, BigDecimal saldoFinal, BigDecimal totalCredito, BigDecimal totalDebito, int totalRegistro) {

}
