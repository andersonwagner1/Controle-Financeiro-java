package br.com.controlefinanceiro.dto;

import java.util.List;

public record DashboardDto(List<BancoDto> bancos, List<ContaResumoDto> contas,
                           List<LancamentoDto> lancamentos) {}
