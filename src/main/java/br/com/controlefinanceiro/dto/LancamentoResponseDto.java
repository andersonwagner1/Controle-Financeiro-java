package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;




import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;
import lombok.Builder;

@Builder
public record LancamentoResponseDto(
        Long id, 
        Long bancoContaId, 
        Long tipoMovimentaocaoId, 
        Long transferenciaId,
        String data,
        EnumTipoMovimentacao tipo,
        String conta,
        String banco,
        String tipoMovimentacao,
        String observacao, 
        BigDecimal valor, 
        BigDecimal saldo
        ){

       
}
