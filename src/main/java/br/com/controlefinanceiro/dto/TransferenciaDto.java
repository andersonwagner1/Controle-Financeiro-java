package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferenciaDto(Long id, Long contaOrigemId, Long contaDestinoId,
                               BigDecimal valor, LocalDate data, String descricao, Long investimentoId) {}