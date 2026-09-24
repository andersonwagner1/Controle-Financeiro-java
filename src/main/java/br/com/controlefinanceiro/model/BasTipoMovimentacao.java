package br.com.controlefinanceiro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.controlefinanceiro.model.emurador.EnumRelatorio;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;




import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;


@Entity
@Table(name = "BAS_TIPO_MOVIMENTACAO")
public class BasTipoMovimentacao {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idTipoMovimentacao")
	@SequenceGenerator(name="idTipoMovimentacao", sequenceName="seq_BasTipMovimentacao", initialValue = 1, allocationSize = 1)
	Long id;
	String dsTipoMovimentacao;
	String dsObservacao;
	
	@ManyToOne
    @JoinColumn(name = "CATEGORIA_ID")
	BasCategoriaMovimentacao categoria;
	
	@Enumerated(EnumType.STRING)
	EnumSimNao icSituacao;
	
	
	@Enumerated(EnumType.STRING)
	EnumTipoMovimentacao icTipoMovimentacao;
	
	
	@Enumerated(EnumType.STRING)
	EnumRelatorio icRelatorio;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDsTipoMovimentacao() {
		return dsTipoMovimentacao;
	}
	public void setDsTipoMovimentacao(String dsTipoMovimentacao) {
		this.dsTipoMovimentacao = dsTipoMovimentacao;
	}
	public String getDsObservacao() {
		return dsObservacao;
	}
	public void setDsObservacao(String dsObservacao) {
		this.dsObservacao = dsObservacao;
	}
	public BasCategoriaMovimentacao getCategoria() {
		return categoria;
	}
	public void setCategoria(BasCategoriaMovimentacao categoria) {
		this.categoria = categoria;
	}
	public EnumSimNao getIcSituacao() {
		return icSituacao;
	}
	public void setIcSituacao(EnumSimNao icSituacao) {
		this.icSituacao = icSituacao;
	}
	public EnumTipoMovimentacao getIcTipoMovimentacao() {
		return icTipoMovimentacao;
	}
	public void setIcTipoMovimentacao(EnumTipoMovimentacao icTipoMovimentacao) {
		this.icTipoMovimentacao = icTipoMovimentacao;
	}
	public EnumRelatorio getIcRelatorio() {
		return icRelatorio;
	}
	public void setIcRelatorio(EnumRelatorio icRelatorio) {
		this.icRelatorio = icRelatorio;
	}
	
}
