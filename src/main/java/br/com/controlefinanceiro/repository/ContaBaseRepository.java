package br.com.controlefinanceiro.repository;
import br.com.controlefinanceiro.model.BasConta;

import org.springframework.data.jpa.repository.JpaRepository;
public interface ContaBaseRepository extends JpaRepository<BasConta, Long> {}
