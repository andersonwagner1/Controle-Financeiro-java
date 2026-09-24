package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.BasInvestimento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvestimentoRepository extends JpaRepository<BasInvestimento, Long> {

    @Query ("SELECT x from BasInvestimento x where x.bancoConta.id = :bancoConta")
    List<BasInvestimento> listarPorBancoConta(Long bancoConta);}