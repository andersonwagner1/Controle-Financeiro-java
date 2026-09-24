package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferenciaDto(Long id, Long bancoContaId, Long bancoContaDestinoId ,
                               BigDecimal valor, LocalDate data, String observacao, Long investimento, String tipoTransferencia) {}