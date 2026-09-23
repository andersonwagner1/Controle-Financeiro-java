package br.com.controlefinanceiro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BasTransferenciaBancoContaId implements Serializable {

  

    @Column(name = "BANCO_CONTA_ID")
    private Long bancoContaId; 

    @Column(name = "TRANSFERIR_ID")
    private Long transferirParaId;



}