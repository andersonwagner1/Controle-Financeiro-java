package br.com.controlefinanceiro.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.math.BigDecimal;

import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;

@Getter 
@Setter
@Entity
@Table(name = "bas_banco_conta")
public class BasBancoConta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_id_banco_conta")
    @SequenceGenerator(
        name = "seq_id_banco_conta", 
        sequenceName = "seq_bas_banco_conta", 
        initialValue = 1, 
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banco_id")
    private BasBanco banco;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id")
    private BasConta conta;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ic_situacao")
    private EnumSimNao icSituacao;

    @Column(name = "ic_tipo_conta")
    private String icTipoConta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abertura_id")
    private BasCompetencia dtAbertura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fechamento_id")
    private BasCompetencia dtFechamento;
    
    @Column(name = "vl_saldo_atual")
    private BigDecimal vlSaldoAtual;
}