package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.BasInvestimento;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestimentoRepository extends JpaRepository<BasInvestimento, Long> {}