package br.com.controlefinanceiro.dto;

import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;

public record TipoMovimentacaoDto(Long id, String nome, EnumTipoMovimentacao tipo, EnumSimNao ativo) {}