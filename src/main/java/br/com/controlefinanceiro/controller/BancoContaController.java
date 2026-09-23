package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.VinculoDto;
import br.com.controlefinanceiro.service.BancoContaService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vinculos")
public class BancoContaController {
    private final BancoContaService service;

    public BancoContaController(BancoContaService service) {
        this.service = service;
    }

    @GetMapping
    public List<VinculoDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public VinculoDto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public VinculoDto criar(@RequestBody VinculoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public VinculoDto atualizar(@PathVariable Long id, @RequestBody VinculoDto dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/saldo")
    public VinculoDto atualizarSaldo(@PathVariable Long id, @RequestParam BigDecimal saldo) {
        return service.atualizarSaldo(id, saldo);
    }
}