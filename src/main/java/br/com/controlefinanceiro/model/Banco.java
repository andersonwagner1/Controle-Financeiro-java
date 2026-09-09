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
@Table(name = "CF_bancos")
public class Banco {
    @Id private String id = UUID.randomUUID().toString();
    private String nome;
    private String logo;
    private String cor;
    private String corSecundaria;
}
