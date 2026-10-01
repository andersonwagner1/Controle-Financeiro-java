package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.ContaResumoDto;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import  br.com.controlefinanceiro.service.LancamentoService;

@RestController
@RequestMapping("/api/contas")
public class ContaController {
    private final LancamentoService service;

    public ContaController(LancamentoService service) {
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