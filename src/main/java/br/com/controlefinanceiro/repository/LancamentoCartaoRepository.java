package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.LancamentoCartao;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoCartaoRepository extends JpaRepository<LancamentoCartao, String> {
    List<LancamentoCartao> findByContaIdOrderByDataDesc(String contaId);

    @Query("select coalesce(sum(l.valor), 0) from LancamentoCartao l "
            + "where l.cartaoCreditoId = :cartaoId and lower(l.tipo) = 'debito'")
    BigDecimal somarComprasPorCartao(@Param("cartaoId") String cartaoId);
}