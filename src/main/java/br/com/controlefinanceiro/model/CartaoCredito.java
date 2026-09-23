package br.com.controlefinanceiro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "CF_cartoes_credito")
public class CartaoCredito {
    @Id private String id = UUID.randomUUID().toString();
    private String nome;
    /** Identificador do vínculo (banco/conta) ao qual o cartão pertence. */
    @ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
    private BasBancoConta bancoConta;
    private BigDecimal limite;
    private Integer diaFechamento;
    private Integer diaVencimento;
    private LocalDate dataFechamento;
    private LocalDate dataAbertura;
}
