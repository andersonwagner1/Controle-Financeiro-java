package br.com.controlefinanceiro.controller;
import br.com.controlefinanceiro.service.MovimentaocaFinalService;
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
       service.atualizarCorrrigirSaldoApartirDoMes(bancoContaId, null);
    }


    @PostMapping("/atualizar-saldo/{bancoContaId}/{dataInicial}")
    public void atualizarSaldo(@PathVariable("bancoContaId")  Long bancoContaId, @PathVariable ("dataInicial") String dataInicial, @RequestBody  Double saldo) {
       service.atualizarSaldo(bancoContaId, dataInicial,saldo);
    }
}