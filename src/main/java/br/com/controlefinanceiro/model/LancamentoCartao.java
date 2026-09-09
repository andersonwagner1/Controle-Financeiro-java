package br.com.controlefinanceiro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "CF_Lancamento_cartao")
public class LancamentoCartao {
    @Id private String id = UUID.randomUUID().toString();
    private String contaId;
    private String cartaoCreditoId;
    private String tipo;
    private String descricao;
    private String categoria;
    private BigDecimal valor;
    private LocalDate data;
    private String observacao;
    private BigDecimal saldoApos;
    private String transferenciaId;
}