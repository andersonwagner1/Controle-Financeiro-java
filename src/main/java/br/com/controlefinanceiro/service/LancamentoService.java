package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.LancamentoDto;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LancamentoService {
    private final FinanceiroService financeiroService;
    private final TransferenciaService transferenciaService;

    public LancamentoService(FinanceiroService financeiroService, TransferenciaService transferenciaService) {
        this.financeiroService = financeiroService;
        this.transferenciaService = transferenciaService;
    }

    public List<LancamentoDto> listar(Long contaId, Date dataInicial, Date dataFinal) {
        return financeiroService.listarLancamentos(contaId, dataInicial, dataFinal);
    }

    public LancamentoDto buscar(Long id) {
        return financeiroService.buscarLancamento(id);
    }

    public LancamentoDto criar(LancamentoDto dto) {
        return financeiroService.criarLancamento(dto);
    }

    public LancamentoDto atualizar(Long id, LancamentoDto dto) {
        return financeiroService.atualizarLancamento(id, dto);
    }

    public void excluir(Long id) {
        financeiroService.excluirLancamento(id);
    }



    public void categorias() {

        
    }
}