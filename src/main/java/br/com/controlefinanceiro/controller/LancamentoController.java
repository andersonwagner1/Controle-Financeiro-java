package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.LancamentoDto;
import br.com.controlefinanceiro.service.LancamentoService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
    private final LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<LancamentoDto> listar(@RequestParam(required = false) String contaId) {
        return service.listar(contaId);
    }

    @GetMapping("/{id}")
    public LancamentoDto buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    public LancamentoDto criar(@RequestBody LancamentoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public LancamentoDto atualizar(@PathVariable String id, @RequestBody LancamentoDto dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable String id) {
        service.excluir(id);
    }
}