package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;


public record LancamentoRequestDto(Long id, Long bancoContaId, 
        Long tipoMovimentacaoId, BigDecimal valor, String data,
        String observacao,  Long transferenciaId){

       
}
