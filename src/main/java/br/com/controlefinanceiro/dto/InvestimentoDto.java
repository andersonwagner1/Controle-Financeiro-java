package br.com.controlefinanceiro.dto;

import br.com.controlefinanceiro.model.emurador.EnumSimNao;

public record InvestimentoDto(Long id, String nome, EnumSimNao ativo) {}