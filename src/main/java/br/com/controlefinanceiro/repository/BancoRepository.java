package br.com.controlefinanceiro.repository;
import br.com.controlefinanceiro.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BancoRepository extends JpaRepository<Banco, String> {}
