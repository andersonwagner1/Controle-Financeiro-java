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
@Table(name = "BAS_LANCAMENTO_CREDITO")
public class BasLancamentoCredito {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idLancamentoCredito")
	@SequenceGenerator(name="idLancamentoCredito", sequenceName="seq_BasLancamentoCredito", initialValue = 1, allocationSize = 1)
	Long id;

	String dsObservacao;
	Date dtMovimentacao;

	@Enumerated (EnumType.STRING)
	EnumSimNao icSituacao;
	Integer nrParcelas;
	BigDecimal vlCompra;
	
	@ManyToOne
	@JoinColumn(name = "COMPETENCIA_ID")
	BasCompetencia competencia;
	
	@ManyToOne
	@JoinColumn(name = "TIPO_MOVIMENTACAO_ID")
	BasTipoMovimentacao tipoMovimentacao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CARTAO_CREDITO_ID")
	CartaoCredito cartaoCredito;
}
