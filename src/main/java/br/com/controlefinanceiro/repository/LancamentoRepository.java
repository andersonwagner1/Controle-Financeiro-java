package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.Lancamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, String> {
    List<Lancamento> findByContaIdOrderByDataDesc(String contaId);
}
