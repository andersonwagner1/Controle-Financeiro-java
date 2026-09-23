package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;




import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;

public record LancamentoDto(Long id, Long bancoContaId, EnumTipoMovimentacao tipo, String descricao,
        String categoria, BigDecimal valor, String data,
        String observacao, BigDecimal saldoApos, Long transferenciaId){
}
