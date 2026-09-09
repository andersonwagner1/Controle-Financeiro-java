package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.CartaoCreditoDto;
import br.com.controlefinanceiro.service.CartaoCreditoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartoes-credito")
public class CartaoCreditoController {
    private final CartaoCreditoService service;

    public CartaoCreditoController(CartaoCreditoService service) {
        this.service = service;
    }

    @GetMapping
    public List<CartaoCreditoDto> listar() {
        List<CartaoCreditoDto> t = service.listar();
        return t;
    }

    @GetMapping("/{id}")
    public CartaoCreditoDto buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartaoCreditoDto criar(@Valid @RequestBody CartaoCreditoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public CartaoCreditoDto atualizar(@PathVariable String id, @Valid @RequestBody CartaoCreditoDto dto) {
        return service.atualizar(id, dto);
    }
}
