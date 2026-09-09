package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.CategoriaDto;
import br.com.controlefinanceiro.service.CategoriaService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoriaDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CategoriaDto buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    public CategoriaDto criar(@RequestBody CategoriaDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public CategoriaDto atualizar(@PathVariable String id, @RequestBody CategoriaDto dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/status")
    public CategoriaDto atualizarStatus(@PathVariable String id, @RequestParam String ativo) {
        return service.atualizarStatus(id, ativo);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable String id) {
        service.excluir(id);
    }
}