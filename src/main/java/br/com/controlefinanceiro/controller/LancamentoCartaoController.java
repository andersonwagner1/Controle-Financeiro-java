package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.LancamentoCartaoDto;
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
    public List<LancamentoCartaoDto> listar(@RequestParam(required = false) String contaId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        return service.listar(contaId).stream()
            .filter(lancamento -> dataInicio == null && dataFim == null
                || lancamento.data() != null
                    && (dataInicio == null || !lancamento.data().isBefore(dataInicio))
                    && (dataFim == null || !lancamento.data().isAfter(dataFim)))
                .toList();
    }

    @GetMapping("/{id}")
    public LancamentoCartaoDto buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoCartaoDto criar(@RequestBody LancamentoCartaoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public LancamentoCartaoDto atualizar(@PathVariable String id, @RequestBody LancamentoCartaoDto dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable String id) {
        service.excluir(id);
    }
}