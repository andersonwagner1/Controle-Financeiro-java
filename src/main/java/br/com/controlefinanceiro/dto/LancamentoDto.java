package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoDto(String id, String contaId, String tipo, String descricao,
        String categoria, BigDecimal valor, LocalDate data,
        String observacao, BigDecimal saldoApos, String transferenciaId) {
}
