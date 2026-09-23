package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.BancoDto;
import br.com.controlefinanceiro.service.BancoService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bancos")
public class BancoController {
    private final BancoService service;

    public BancoController(BancoService service) {
        this.service = service;
    }

    @GetMapping
    public List<BancoDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public BancoDto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public BancoDto criar(@RequestBody BancoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public BancoDto atualizar(@PathVariable Long id, @RequestBody BancoDto dto) {
        return service.atualizar(id, dto);
    }
}