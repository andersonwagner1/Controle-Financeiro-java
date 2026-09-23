package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.BasMovimentacao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoRepository extends JpaRepository<BasMovimentacao, Long> {
    
        @Query("select l from BasMovimentacao l "
        + "where (:contaId is null or l.bancoConta.id = :contaId) "
        + "order by l.dtMovimentacao desc")
        List<BasMovimentacao> findByContaIdOrderByDataDesc(@Param("contaId") Long contaId);
@Query("select l from BasMovimentacao l "
     + " WHERE l.bancoConta.id = :contaId "
     + " AND l.dtMovimentacao >= :dataInicial "
     + " AND l.dtMovimentacao <= :dataFinal "
     + " order by l.dtMovimentacao desc")
List<BasMovimentacao> buscarPorFiltros(
    @Param("contaId") Long contaId,
    @Param("dataInicial") Date dataInicial, 
    @Param("dataFinal") Date dataFinal
);

@Query("select l from BasMovimentacao l "       
     + " WHERE l.dtMovimentacao between :dataInicial AND :dataFinal "
     
     + " order by l.dtMovimentacao desc")
List<BasMovimentacao> buscarPorFiltrosTodasBancos(
    @Param("dataInicial") Date dataInicial, 
    @Param("dataFinal") Date dataFinal
);



        
@Query(value = "          SELECT bm.banco_conta_id, BM.dt_movimentacao ,  " + 
        	"    case when BM.vl_debito IS null  " + 
"            		or BM.vl_debito =0  " + 
        			"    then BM.vl_credito   " + 
        			"    else BM.vl_debito   " + 
     "       		end as VALOR, " + 
    "            		BM.tipo_movimentacao_id,    " + 
"            		bm.ds_observacao, t.ds_tipo_movimentacao,t.ic_tipo_movimentacao, " + 
     "       		BMF.BANCO_CONTA_ID as DESTINO, " + 
        "    		BMF.investimento_id " + 

"            		from bas_movimentacao bm " +  
        		"    inner join bas_tipo_movimentacao t on t.id = bm.tipo_movimentacao_id  " + 
        		"    left join BAS_MOVIMENTACAO BMF on BMF.ID = BM.vinculado_id  " + 
        		"    WHERE bm.ic_calcular  = 'SIM' and BM.ic_situacao ='SIM'        	 " + 	
        		"    and bm.DT_MOVIMENTACAO BETWEEN TO_DATE(CONCAT(2010, '-01-01'), 'YYYY-MM-DD') " + 
                                 "    AND TO_DATE(CONCAT((2010), '-12-31'), 'YYYY-MM-DD') " + 
                                 "    and (IC_tipo_movimentacao in ('CREDITO','DEBITO') " + 
                                 "    OR (IC_tipo_movimentacao in ('RESGATE','TRANSFERENCIA') and BM.vl_debito >0)" + 
                                 "    or (IC_tipo_movimentacao in ('APLICACAO') and BM.vl_credito  >0))  " + 
                                 "    ORDER BY BM.DT_MOVIMENTACAO ASC        ", nativeQuery = true)
List<Object[]> registrar(@Param("ano") Integer ano);


}
