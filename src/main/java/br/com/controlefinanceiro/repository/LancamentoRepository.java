package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.Lancamento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoRepository extends JpaRepository<Lancamento, String> {
    List<Lancamento> findByContaIdOrderByDataDesc(String contaId);

@Query("select l from Lancamento l "
        + "where (:contaId is null or l.contaId = :contaId) "
        + "and (cast(:dataInicial as date) is null or l.data >= :dataInicial) "
        + "and (cast(:dataFinal as date) is null or l.data <= :dataFinal) "
        + "order by l.data desc")
List<Lancamento> buscarPorFiltros(@Param("contaId") String contaId,
        @Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal);
}
