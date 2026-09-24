package br.com.controlefinanceiro.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.controlefinanceiro.model.BasCompetencia;

public interface BasCompetenciaRespository extends JpaRepository<BasCompetencia, Long> {

    @Query ("SELECT x FROM BasCompetencia x Where x.nrMes = :mes and x.nrAno = :ano")
    BasCompetencia consultaPorMesAno(Integer mes, Integer ano);
}
