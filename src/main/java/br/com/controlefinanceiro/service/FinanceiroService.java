package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.repository.*;
import org.springframework.stereotype.Service;

@Service
public class FinanceiroService {
    private final ContaBaseRepository contasBaseRepository;
    private final BancoContaRepository vinculosRepository;


    public FinanceiroService(BancoRepository bancos, ContaBaseRepository contasBase,
            TipoMovimentacaoRepository categorias, 
            BancoContaRepository vinculos, 
            InvestimentoRepository investimentos,
            LancamentoRepository lancamentos,
            BasMovimentacaoFinalRepository movimentacaoFinalRepostory,


        BasCompetenciaRespository competencias) {
        this.contasBaseRepository = contasBase;
        this.vinculosRepository = vinculos;

    }


}

