package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.TransferenciaDto;
import org.springframework.stereotype.Service;

@Service
public class TransferenciaService {
    private final FinanceiroService financeiroService;

    public TransferenciaService(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    public TransferenciaDto criar(TransferenciaDto dto) {
        return financeiroService.transferir(dto);
    }
}