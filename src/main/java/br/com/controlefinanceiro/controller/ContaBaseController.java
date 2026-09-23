package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.ContaBaseDto;
import br.com.controlefinanceiro.service.ContaBaseService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contas-base")
public class ContaBaseController {
    private final ContaBaseService service;

    public ContaBaseController(ContaBaseService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContaBaseDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ContaBaseDto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ContaBaseDto criar(@RequestBody ContaBaseDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public ContaBaseDto atualizar(@PathVariable Long id, @RequestBody ContaBaseDto dto) {
        return service.atualizar(id, dto);
    }
}