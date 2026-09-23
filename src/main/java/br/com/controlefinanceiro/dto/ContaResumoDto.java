package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.controlefinanceiro.model.emurador.EnumSimNao;

public record ContaResumoDto(Long id, Long bancoId, String tipo, String descricao,
                             BigDecimal saldo, EnumSimNao ativa, BigDecimal rentabilidade,
                             LocalDate vencimento, LocalDate dataAbertura) {
        //TODO Auto-generated constructor stub
    }