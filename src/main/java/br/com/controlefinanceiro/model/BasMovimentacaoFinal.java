package br.com.controlefinanceiro.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;

@Data 
@Entity
@Table(name = "BAS_MOVIMENTACAO_FINAL")
public class BasMovimentacaoFinal {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idMovimentacaoFinal")
	@SequenceGenerator(name="idMovimentacaoFinal", sequenceName="seq_BasMovimentacaoFinal", initialValue = 1, allocationSize = 1)
	Long id;
	
	@ManyToOne
    @JoinColumn(name = "COMPETENCIA_ID")
	BasCompetencia competencia;
	
	@ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
	BasBancoConta bancoConta;
	
	BigDecimal vlSaldoInicial;
	BigDecimal vlSaldoFinal;
	BigDecimal vlTotalCredito;
	BigDecimal vlTotalDebito;
	
	BigDecimal vlEsperado;
	BigDecimal vlEsperadoCredito;
	BigDecimal vlEsperadoDebito;
	
	@Enumerated(EnumType.STRING)
	EnumSimNao icFechado = EnumSimNao.NAO;
	
}
