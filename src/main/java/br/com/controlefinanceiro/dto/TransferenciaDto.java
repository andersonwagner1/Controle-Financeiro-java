package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferenciaDto(String id, String contaOrigemId, String contaDestinoId,
                               BigDecimal valor, LocalDate data, String descricao, String investimentoId) {}