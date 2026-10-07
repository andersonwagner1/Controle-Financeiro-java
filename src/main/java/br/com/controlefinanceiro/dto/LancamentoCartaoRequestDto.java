package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;


@Builder 
public record LancamentoCartaoRequestDto(
        Long id,
        Long cartaoCreditoId,
        Long tipoMovimentacaoId,
        String descricao,
        BigDecimal valor,
        Integer parcelas,
        Date data
     ) {
}