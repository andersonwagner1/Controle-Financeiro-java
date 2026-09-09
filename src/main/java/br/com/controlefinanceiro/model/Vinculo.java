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
@Table(name = "CF_vinculos")
public class Vinculo {
    @Id private String id = UUID.randomUUID().toString();
    private String bancoId;
    private String contaBaseId;
    private BigDecimal saldo = BigDecimal.ZERO;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal rentabilidade;
    private LocalDate vencimento;
    private boolean ativa = true;
}
