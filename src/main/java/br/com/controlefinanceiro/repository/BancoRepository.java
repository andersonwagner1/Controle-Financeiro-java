package br.com.controlefinanceiro.repository;
import br.com.controlefinanceiro.model.BasBanco;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BancoRepository extends JpaRepository<BasBanco, Long> {}
