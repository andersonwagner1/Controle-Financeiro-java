package br.com.controlefinanceiro.model;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter 
@Setter 
@Entity
@Table(name = "BAS_BANCO")
public class BasBanco {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idBanco")
	@SequenceGenerator(name="idBanco", sequenceName="seq_BasBanco", initialValue = 1, allocationSize = 1)
	Long id;
	String dsBanco;
	String dsDescricao;
	String logo;
	String cor;
	String corSecundaria;
	
	
	@Enumerated(EnumType.STRING)
	EnumSimNao icSituacao;
	
	@Enumerated(EnumType.STRING)
	EnumSimNao icCartaoCredito;
	
	

	

}
