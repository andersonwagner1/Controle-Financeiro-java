package br.com.controlefinanceiro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "CF_Contas")
public class ContaBase {
    @Id private String id = UUID.randomUUID().toString();
    private String descricao;
    private String tipo;
}
