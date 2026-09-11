package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestimentoRepository extends JpaRepository<Investimento, String> {}