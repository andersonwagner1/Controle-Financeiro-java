package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VinculoDto(String id, String bancoId, String contaBaseId, BigDecimal saldo,
                         LocalDate dataInicio, LocalDate dataFim, BigDecimal rentabilidade,
                         LocalDate vencimento, Boolean ativa) {}