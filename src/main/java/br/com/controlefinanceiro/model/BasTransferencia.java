package br.com.controlefinanceiro.model;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;


@Data 
@Entity
@Table(name = "BAS_TRANSFERENCIA")
public class BasTransferencia {


	@EmbeddedId
    private BasTransferenciaBancoContaId id;


	// Caso os IDs sejam chaves estrangeiras (FK) para a entidade BancoConta:
    @ManyToOne
    @MapsId("bancoContaId")
    @JoinColumn(name = "BANCO_CONTA_ID")
    private BasBancoConta bancoConta;

    @ManyToOne
    @MapsId("transferirParaId")
    @JoinColumn(name = "TRANSFERIR_ID")
    private BasBancoConta transferirPara;

    @Enumerated(EnumType.STRING)
    @Column(name = "IC_SITUACAO")
    private EnumSimNao icSituacao;


	
		
}
