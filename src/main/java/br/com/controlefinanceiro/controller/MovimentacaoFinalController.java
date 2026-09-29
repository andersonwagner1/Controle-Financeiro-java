package br.com.controlefinanceiro.controller;


import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.service.MovimentaocaFinalService;
import jakarta.websocket.server.PathParam;

import java.util.Date;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movimentacao-final")
public class MovimentacaoFinalController {
    private final MovimentaocaFinalService service;

    public MovimentacaoFinalController(MovimentaocaFinalService service){
        this.service = service;
    }

    @PostMapping("/atualizar/{bancoContaId}/{data}")
    public void atualizar(@PathVariable ("bancoContaId")  Long bancoContaId, @PathVariable("data")String data) {
       service.atualizarMovimentacaoGeral(bancoContaId, data);
    }


        @PostMapping("/atualizar-saldo/{bancoContaId}/{dataInicial}")
    public void atualizarSaldo(@PathParam("bancoId")  Long bancoId, @PathParam("dataInicial")Date dataInicial, @RequestBody  Double saldo) {
       ///service.atualizarMovimentacaoGeral(bancoId, contaId);
    }

    


}