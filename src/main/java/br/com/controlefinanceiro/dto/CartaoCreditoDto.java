package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CartaoCreditoDto(
                Long id,
                String nome,
                Long vinculoId,
                BigDecimal limite,
                Integer diaFechamento,
                Integer diaVencimento,
                BigDecimal limiteDisponivel,
                LocalDate dataFechamento,
                LocalDate dataAbertura
            ) {

}
