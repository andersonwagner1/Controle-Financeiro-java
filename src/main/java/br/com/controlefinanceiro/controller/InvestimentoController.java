package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.InvestimentoDto;
import br.com.controlefinanceiro.service.InvestimentoService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investimentos")
public class InvestimentoController {
    private final InvestimentoService service;

    public InvestimentoController(InvestimentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<InvestimentoDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public InvestimentoDto buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    public InvestimentoDto criar(@RequestBody InvestimentoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public InvestimentoDto atualizar(@PathVariable String id, @RequestBody InvestimentoDto dto) {
        return service.atualizar(id, dto);
    }
}