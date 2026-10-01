package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.*;
import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FinanceiroService {
    private final BancoRepository bancoRepsoitory;
    private final ContaBaseRepository contasBaseRepository;
    private final TipoMovimentacaoRepository categoriasRepository;
    private final BancoContaRepository vinculosRepository;
    private final InvestimentoRepository investimentosRepository;
    private final LancamentoRepository lancamentosRepository;
    private final BasCompetenciaRespository competenciasRepository;
    private final BasMovimentacaoFinalRepository movimentacaoFinalRepostory;

    public FinanceiroService(BancoRepository bancos, ContaBaseRepository contasBase,
            TipoMovimentacaoRepository categorias, 
            BancoContaRepository vinculos, 
            InvestimentoRepository investimentos,
            LancamentoRepository lancamentos,
            BasMovimentacaoFinalRepository movimentacaoFinalRepostory,


        BasCompetenciaRespository competencias) {
        this.bancoRepsoitory = bancos;
        this.contasBaseRepository = contasBase;
        this.categoriasRepository = categorias;
        this.vinculosRepository = vinculos;
        this.investimentosRepository = investimentos;
        this.lancamentosRepository = lancamentos;
        this.competenciasRepository = competencias;
        this.movimentacaoFinalRepostory = movimentacaoFinalRepostory;
    }


    
    public List<BancoDto> listarBancos() {
        return bancoRepsoitory.findAll().stream().map(this::toDto).toList();
    }

    public List<ContaResumoDto> listarContas() {
        return vinculosRepository.findAll().stream().map(vinculo -> {
            BasConta base = contasBaseRepository.findById(vinculo.getConta().getId()).orElse(null);
            return new ContaResumoDto(vinculo.getId(), vinculo.getBanco().getId(), base == null ? null : base.getIcTipo(),
                    base == null ? null : base.getDsConta(), vinculo.getVlSaldoAtual(), vinculo.getIcSituacao(),
                    null, 
                    DateUtils.getUltimoDiaDoMes(vinculo.getDtFechamento().getDsMesAno()), 
                    DateUtils.getPrimeiroDiaDoMes(vinculo.getDtAbertura().getDsMesAno()));
        }).toList();
    }

    public ContaResumoDto buscarConta(String id) {
        return listarContas().stream().filter(conta -> id.equals(conta.id()))
                .findFirst().orElseThrow(() -> naoEncontrado("Conta"));
    }

   

    



   



    private BancoDto toDto(BasBanco e) {
        return new BancoDto(e.getId(), e.getDsBanco(), e.getLogo(), e.getCor(), e.getCorSecundaria());
    }


    /**
     * Converter Dto para Entity
     * @param dto
     * @return
     */
    

    


    private ResponseStatusException naoEncontrado(String nome) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, nome + " não encontrado");
    }
}

