package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoCartaoDto(
        String id, 
        String vinculoId,
        String tipo, 
        String descricao,
         String categoria, 
         BigDecimal valor, 
         LocalDate data,
        String observacao
        ) {
}