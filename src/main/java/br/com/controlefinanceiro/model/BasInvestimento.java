package br.com.controlefinanceiro.model;

import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "BAS_INVESTIMENTO")
public class BasInvestimento {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idInvestimento")
	@SequenceGenerator(name="idInvestimento", sequenceName="seq_BasInvestimento", initialValue = 1, allocationSize = 1)
	Long id;
	
	@ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
	BasBancoConta bancoConta;

	@Column(name = "DS_INVESTIMENTO")
	String dsInvestimento;

	@Column(name = "IC_SITUACAO")
	EnumSimNao icSituacao;
	
	
	
}
