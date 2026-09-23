package br.com.controlefinanceiro.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import br.com.controlefinanceiro.model.emurador.EnumSimNao;
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
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@Entity
@Table(name = "BAS_BANCO_CONTA")
public class BasBancoConta {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idBancoConta")
	@SequenceGenerator(name="idBancoConta", sequenceName="seq_BasBancoConta", initialValue = 1, allocationSize = 1)
	Long id;
	@ManyToOne
    @JoinColumn(name = "BANCO_ID")
	BasBanco banco;
	
	@ManyToOne
    @JoinColumn(name = "CONTA_ID")
	BasConta conta;
	
	@Enumerated (EnumType.STRING)
	EnumSimNao icSituacao;


	String icTipoConta;
	
	@ManyToOne
    @JoinColumn(name = "ABERTURA_ID")
	BasCompetencia dtAbertura;
	@ManyToOne
    @JoinColumn(name = "FECHAMENTO_ID")
	BasCompetencia dtFechamento;

	//novo campo
	BigDecimal vlSaldoAtual;

	
	
	
}
