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
@Table(name = "CF_INVESTIMENTO")
public class Investimento {
    @Id private String id = UUID.randomUUID().toString();
    private String nome;
    private boolean ativo;

}