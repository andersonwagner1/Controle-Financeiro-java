package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;

public record RelatorioMensalDto(
        String competencia,
        String mes,
        BigDecimal saldoAnterior,
        BigDecimal resgate,
        BigDecimal renda,
        BigDecimal credito,
        BigDecimal rendimento,
        BigDecimal totalCredito,
        BigDecimal rendimentoNegativo,
        BigDecimal cartao,
        BigDecimal aplicacao,
        BigDecimal debito,
        BigDecimal mensal,
        BigDecimal totalDebito,
        BigDecimal saldoFinal) {
}