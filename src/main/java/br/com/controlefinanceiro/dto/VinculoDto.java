package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;



public record VinculoDto(Long id, Long bancoId, Long contaBaseId, BigDecimal saldo,
                         LocalDate dataInicio, LocalDate dataFim, BigDecimal rentabilidade,
                         LocalDate vencimento, Boolean ativa) {

        //TODO Auto-generated constructor stub
    }