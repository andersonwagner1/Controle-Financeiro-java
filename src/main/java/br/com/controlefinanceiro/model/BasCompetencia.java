package br.com.controlefinanceiro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "BAS_COMPETENCIA")
public class BasCompetencia {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Competencia")
	@SequenceGenerator(name="id_Competencia", sequenceName="seq_Bas_Competencia", initialValue = 1, allocationSize = 1)
	Long id;
	@Column(name = "NR_ANO")
	Integer nrAno;
	@Column(name = "NR_MES")
	Integer nrMes;
	@Column(name = "DS_MES_ANO")
	String dsMesAno;
	
	public void proximoMes(int proximo) {
		nrMes++;
		if (nrMes > 12) {
			nrAno++;
			nrMes = 1;
		}
	}
	
	
	public void anteriorMes() {
		nrMes--;
		if (nrMes == 0) {
			nrAno--;
			nrMes = 12;
		}
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getNrAno() {
		return nrAno;
	}
	public void setNrAno(Integer nrAno) {
		this.nrAno = nrAno;
	}
	public Integer getNrMes() {
		return nrMes;
	}
	public void setNrMes(Integer nrMes) {
		this.nrMes = nrMes;
	}
	public String getDsMesAno() {
		return dsMesAno;
	}
	public void setDsMesAno(String dsMesAno) {
		this.dsMesAno = dsMesAno;
	}
	
	
	
	

}
