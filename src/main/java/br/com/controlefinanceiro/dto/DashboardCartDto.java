package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;

public record DashboardCartDto(
        BigDecimal patrimonioTotal,
        BigDecimal saldoBancario,
        BigDecimal investimentos,
        BigDecimal creditosMes,
        BigDecimal debitosMes,
        BigDecimal saldoMes) {
}