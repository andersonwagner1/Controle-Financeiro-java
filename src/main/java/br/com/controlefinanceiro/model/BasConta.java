package br.com.controlefinanceiro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;

@Data 
@Entity
@Table(name = "BAS_CONTA")
public class BasConta {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idConta")
	@SequenceGenerator(name="idConta", sequenceName="seq_BasConta", initialValue = 1, allocationSize = 1)
	Long id;
	String dsConta;
	String dsObservacao;
	@Enumerated(EnumType.STRING)
	EnumSimNao icSituacao;

	String icTipo;
	
	
	

}
