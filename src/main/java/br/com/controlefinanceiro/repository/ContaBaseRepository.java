package br.com.controlefinanceiro.repository;
import br.com.controlefinanceiro.model.ContaBase;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ContaBaseRepository extends JpaRepository<ContaBase, String> {}
