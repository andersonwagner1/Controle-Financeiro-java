package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.DashboardDto;
import br.com.controlefinanceiro.service.FinanceiroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final FinanceiroService service;

    public DashboardController(FinanceiroService service) {
        this.service = service;
    }

    @GetMapping
    public DashboardDto buscarDados() {
        return new DashboardDto(
                service.listarBancos(),
                service.listarContas(),
                service.listarLancamentos(null));
    }
}
