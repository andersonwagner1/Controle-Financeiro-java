package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.TransferenciaDto;
import br.com.controlefinanceiro.service.TransferenciaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaController {
    private final TransferenciaService service;

    public TransferenciaController(TransferenciaService service) {
        this.service = service;
    }

    @PostMapping
    public void criar(@RequestBody TransferenciaDto dto) {
        service.criar(dto);
       // return 
    }
}