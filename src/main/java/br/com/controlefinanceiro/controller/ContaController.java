package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.ContaResumoDto;
import br.com.controlefinanceiro.service.FinanceiroService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contas")
public class ContaController {
    private final FinanceiroService service;

    public ContaController(FinanceiroService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContaResumoDto> listar() {
        return service.listarContas();
    }

    @GetMapping("/{id}")
    public ContaResumoDto buscar(@PathVariable String id) {
        return service.buscarConta(id);
    }
}