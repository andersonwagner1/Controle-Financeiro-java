package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.LancamentoDto;
import br.com.controlefinanceiro.model.BasMovimentacaoFinal;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.service.LancamentoService;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
    private final LancamentoService service;


    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    @GetMapping("/lista")
    public List<LancamentoDto> listar(@RequestParam(required = false) Long contaId,
         String dataInicial,
         String dataFinal) {
            Date dInicial = DateUtils.stringToDate(dataInicial);
            Date dFinal = DateUtils.stringToDate(dataFinal);

        return service.listar(contaId, dInicial, dFinal);
        
    }


    


    @GetMapping("/saldo")
    public BasMovimentacaoFinal listar(@RequestParam(required = false) Long contaId,  String dataInicial)
          {
            Date dInicial = DateUtils.stringToDate(dataInicial);
        return service.consultarSaldo(contaId, dInicial);
        
    }

    @GetMapping("/detalhes/{id}")
    public LancamentoDto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public LancamentoDto criar(@RequestBody LancamentoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public LancamentoDto atualizar(@PathVariable Long id, @RequestBody LancamentoDto dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }


}