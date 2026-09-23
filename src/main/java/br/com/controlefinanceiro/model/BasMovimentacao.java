package br.com.controlefinanceiro.model;

import java.math.BigDecimal;
import java.util.Date;

import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data 
@Entity
@Table(name = "BAS_MOVIMENTACAO")
public class BasMovimentacao {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idMovimentacao")
	@SequenceGenerator(name="idMovimentacao", sequenceName="seq_BasMovimentacao", initialValue = 1, allocationSize = 1)
	Long id;

	Integer nrPosicao;
	Date dtMovimentacao;
	Integer nrDia;
	BigDecimal vlCredito;
	BigDecimal vlDebito;
	BigDecimal vlSaldo;
	String dsObservacao;
	@Enumerated (EnumType.STRING)
	EnumSimNao icCalcular;

	@Enumerated (EnumType.STRING)
	EnumSimNao icSituacao;

	@ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
	BasBancoConta bancoConta;
	
	@ManyToOne
    @JoinColumn(name = "COMPETENCIA_ID")
	BasCompetencia competencia;
	
	@ManyToOne
    @JoinColumn(name = "TIPO_MOVIMENTACAO_ID")
	BasTipoMovimentacao tipoMovimentacao;
	
	
    @JoinColumn(name = "VINCULADO_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	BasMovimentacao vinculado;
	
    @JoinColumn(name = "INVESTIMENTO_ID")
   	@ManyToOne(fetch = FetchType.LAZY)
   	BasInvestimento investimento;
    
	
	
	public BasMovimentacao() {
		super();
		// TODO Auto-generated constructor stub
	}





}
