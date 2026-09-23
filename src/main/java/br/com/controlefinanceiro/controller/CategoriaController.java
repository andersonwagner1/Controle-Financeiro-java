package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.TipoMovimentacaoDto;
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
    public List<TipoMovimentacaoDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public TipoMovimentacaoDto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public TipoMovimentacaoDto criar(@RequestBody TipoMovimentacaoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public TipoMovimentacaoDto atualizar(@PathVariable Long id, @RequestBody TipoMovimentacaoDto dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/status")
    public TipoMovimentacaoDto atualizarStatus(@PathVariable Long id, @RequestParam String ativo) {
        return service.atualizarStatus(id, ativo);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}