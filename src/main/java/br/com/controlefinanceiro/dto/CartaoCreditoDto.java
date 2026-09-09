package br.com.controlefinanceiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CartaoCreditoDto(
                String id,
                @NotBlank String nome,
                @NotBlank String vinculoId,
                @NotNull @DecimalMin(value = "0.01") BigDecimal limite,
                @NotNull @Min(1) @Max(31) Integer diaFechamento,
                @NotNull @Min(1) @Max(31) Integer diaVencimento,
                BigDecimal limiteDisponivel,
                LocalDate dataFechamento,
                LocalDate dataAbertura

            ) {
}
