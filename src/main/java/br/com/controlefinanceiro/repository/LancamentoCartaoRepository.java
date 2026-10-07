package br.com.controlefinanceiro.repository;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.controlefinanceiro.model.BasLancamentoCredito;

public interface LancamentoCartaoRepository extends JpaRepository<BasLancamentoCredito, Long> {

    @Query ("select l from BasLancamentoCredito l inner join l.cartaoCredito where l.cartaoCredito.id = :contaId order by l.dtMovimentacao desc")
    List<BasLancamentoCredito> localizarLancamentoPorCartao(@Param("contaId") Long contaId);

    @Query("select coalesce(sum(l.vlCompra), 0) from BasLancamentoCredito l "
            + " where l.cartaoCredito.id = :cartaoId")
    BigDecimal somarComprasPorCartao(@Param("cartaoId") Long cartaoId);

 @Query ("select l from BasLancamentoCredito l inner join l.cartaoCredito " +
 " where l.dtMovimentacao >= :dataInicio and l.dtMovimentacao <= :dataFim  " +
 " order by l.dtMovimentacao desc")
    List<BasLancamentoCredito> localizarLancamentoPorCartao(Date dataInicio, Date dataFim);
 
 
  @Query ("select l from BasLancamentoCredito l inner join l.cartaoCredito " +
 " where l.dtMovimentacao >= :dataInicio and l.dtMovimentacao <= :dataFim  " +
 " and l.cartaoCredito.id = :contaId order by l.dtMovimentacao desc")
    List<BasLancamentoCredito> localizarLancamentoPorCartao(Long contaId, Date dataInicio, Date dataFim);
}