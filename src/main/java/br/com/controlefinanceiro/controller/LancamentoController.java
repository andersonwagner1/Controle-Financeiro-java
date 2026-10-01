package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.LancamentoResponseDto;
import br.com.controlefinanceiro.dto.SaldoResponseDto;
import br.com.controlefinanceiro.dto.LancamentoRequestDto;
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


     @PostMapping
    public void criar(@RequestBody LancamentoRequestDto dto) {
        service.realizarLancamento(dto);
        
    }



    /**
     * Lista todos os registros
     * @param contaId
     * @param dataInicial
     * @param dataFinal
     * @return
     */
    
    @GetMapping("/lista")
    public List<LancamentoResponseDto> listar(@RequestParam(required = false) Long contaId,
         String dataInicial,
         String dataFinal) {
            Date dInicial = DateUtils.stringToDate(dataInicial);
            Date dFinal = DateUtils.stringToDate(dataFinal);

        return service.listar(contaId, dInicial, dFinal);
        
    }


    /**
     
     * @param contaId
     * @param dataInicial
     * @return
     */
    @GetMapping("/saldo")
    public SaldoResponseDto consultarSaltoFinal(@RequestParam(required = false) Long contaId,  String dataInicial){
        Integer[] competencia = DateUtils.getDiaMesAno(dataInicial);
        return service.consultarSaldo(contaId, competencia);        
    }

    @GetMapping("/detalhes/{id}")
    public LancamentoResponseDto buscar(@PathVariable Long id) {
        return service.consultarRegistros(id);
    }


    @PutMapping("/{id}")
    public LancamentoResponseDto atualizar(@PathVariable Long id, @RequestBody LancamentoRequestDto dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }


}