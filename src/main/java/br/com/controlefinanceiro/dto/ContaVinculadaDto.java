package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;

public record ContaVinculadaDto(Long id, Long bancoId, String bancoNome,
                                Long contaBaseId, String contaNome,
                                BigDecimal saldo, boolean ativa) {}