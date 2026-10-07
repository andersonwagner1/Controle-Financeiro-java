package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.LancamentoCartaoRequestDto;
import br.com.controlefinanceiro.dto.LancamentoCartaoResponseDto;
import br.com.controlefinanceiro.service.LancamentoCartaoService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lancamentos-cartao")
public class LancamentoCartaoController {
    private final LancamentoCartaoService service;

    public LancamentoCartaoController(LancamentoCartaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<LancamentoCartaoResponseDto> listar(@RequestParam(required = false) Long contaId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
         List<LancamentoCartaoResponseDto> listar = service.listar(dataInicio, dataFim, contaId);


        return listar;
    }

    @GetMapping("/{id}")
    public LancamentoCartaoResponseDto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoCartaoResponseDto criar(@RequestBody LancamentoCartaoRequestDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public LancamentoCartaoResponseDto atualizar(@PathVariable Long id, @RequestBody LancamentoCartaoRequestDto dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}