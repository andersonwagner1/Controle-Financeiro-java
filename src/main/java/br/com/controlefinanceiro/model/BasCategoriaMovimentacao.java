package br.com.controlefinanceiro.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

 
@Entity
@Table(name = "BAS_CATEGORIA_MOVIMENTACAO")
public class BasCategoriaMovimentacao {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idCategoriaMovimentacao")
	@SequenceGenerator(name="idCategoriaMovimentacao", sequenceName="seq_BasCategoriaMovimentacao", initialValue = 1, allocationSize = 1)
	Long id;
	String dsCategoria;
	String dsObservacao;
	String icSituacao;
	String icTipo;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDsCategoria() {
		return dsCategoria;
	}
	public void setDsCategoria(String dsCategoria) {
		this.dsCategoria = dsCategoria;
	}
	public String getDsObservacao() {
		return dsObservacao;
	}
	public void setDsObservacao(String dsObservacao) {
		this.dsObservacao = dsObservacao;
	}
	public String getIcSituacao() {
		return icSituacao;
	}
	public void setIcSituacao(String icSituacao) {
		this.icSituacao = icSituacao;
	}
	public String getIcTipo() {
		return icTipo;
	}
	public void setIcTipo(String icTipo) {
		this.icTipo = icTipo;
	}
	
	


    
}
