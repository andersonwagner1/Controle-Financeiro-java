package br.com.controlefinanceiro.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.controlefinanceiro.model.BasTipoMovimentacao;
public interface TipoMovimentacaoRepository extends JpaRepository<BasTipoMovimentacao, Long> {



    @Query ("SELECT x FROM BasTipoMovimentacao x WHERE x.dsTipoMovimentacao = :categoria ")
    BasTipoMovimentacao consultarPorNome(String categoria);}
