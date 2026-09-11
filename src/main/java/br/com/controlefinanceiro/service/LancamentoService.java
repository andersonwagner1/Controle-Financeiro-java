package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.LancamentoDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LancamentoService {
    private final FinanceiroService financeiroService;

    public LancamentoService(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    public List<LancamentoDto> listar(String contaId, LocalDate dataInicial, LocalDate dataFinal) {
        return financeiroService.listarLancamentos(contaId, dataInicial, dataFinal);
    }

    public LancamentoDto buscar(String id) {
        return financeiroService.buscarLancamento(id);
    }

    public LancamentoDto criar(LancamentoDto dto) {
        return financeiroService.criarLancamento(dto);
    }

    public LancamentoDto atualizar(String id, LancamentoDto dto) {
        return financeiroService.atualizarLancamento(id, dto);
    }

    public void excluir(String id) {
        financeiroService.excluirLancamento(id);
    }
}