package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;
import lombok.Builder;


@Builder 
public record LancamentoCartaoResponseDto(
     Long id,
        Long cartaoCreditoId,
        Long tipoMovimentacaoId,
        String descricao,
        String tipoMovimentacao,
        EnumTipoMovimentacao tipo,
        String cartaoCredito,
        BigDecimal valor,
        LocalDate data
        ) 
        {}

