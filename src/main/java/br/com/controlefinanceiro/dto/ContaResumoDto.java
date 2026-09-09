package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContaResumoDto(String id, String bancoId, String tipo, String descricao,
                             BigDecimal saldo, boolean ativa, BigDecimal rentabilidade,
                             LocalDate vencimento, LocalDate dataAbertura) {}