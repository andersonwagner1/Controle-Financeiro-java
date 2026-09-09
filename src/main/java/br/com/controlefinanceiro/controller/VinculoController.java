package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.VinculoDto;
import br.com.controlefinanceiro.service.VinculoService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vinculos")
public class VinculoController {
    private final VinculoService service;

    public VinculoController(VinculoService service) {
        this.service = service;
    }

    @GetMapping
    public List<VinculoDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public VinculoDto buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    public VinculoDto criar(@RequestBody VinculoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public VinculoDto atualizar(@PathVariable String id, @RequestBody VinculoDto dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/saldo")
    public VinculoDto atualizarSaldo(@PathVariable String id, @RequestParam BigDecimal saldo) {
        return service.atualizarSaldo(id, saldo);
    }
}