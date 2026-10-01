package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.DashboardDto;
import br.com.controlefinanceiro.service.LancamentoService;
import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final LancamentoService service;

    public DashboardController(LancamentoService service) {
        this.service = service;
    }

    @GetMapping
    public DashboardDto buscarDados(@RequestParam(required = false) Date dataInicial,
            @RequestParam(required = false) Date dataFinal) {
        return new DashboardDto(null,null
                /*service.listarBancos()*/,
                /*service.listarContas()*/
                null); //service.listarLancamentos(null, dataInicial, dataFinal));
    }
}
