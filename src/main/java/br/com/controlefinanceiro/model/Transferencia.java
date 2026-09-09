package br.com.controlefinanceiro.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transferencia(String id, String contaOrigemId, String contaDestinoId,
                            BigDecimal valor, LocalDate data, String descricao) {}
