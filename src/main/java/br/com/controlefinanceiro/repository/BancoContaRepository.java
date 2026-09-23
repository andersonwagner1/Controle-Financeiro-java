package br.com.controlefinanceiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.controlefinanceiro.model.BasBancoConta;
public interface BancoContaRepository extends JpaRepository<BasBancoConta, Long> {

    
}
