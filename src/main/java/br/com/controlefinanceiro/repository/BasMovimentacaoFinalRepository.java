package br.com.controlefinanceiro.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.controlefinanceiro.model.BasMovimentacaoFinal;
public interface BasMovimentacaoFinalRepository extends JpaRepository<BasMovimentacaoFinal, Long> {

    @Query ("SELECT x FROM BasMovimentacaoFinal x " +
    " WHERE x.bancoConta.id = :bancoContaId " +
    " AND x.competencia.nrAno=:ano " +
    " AND x.competencia.nrMes=:mes ")
    BasMovimentacaoFinal findByBancoContaId(Long bancoContaId, Integer mes, Integer ano);


}
