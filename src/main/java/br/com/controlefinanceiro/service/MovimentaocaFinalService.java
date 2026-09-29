package br.com.controlefinanceiro.service;


import br.com.controlefinanceiro.model.BasBancoConta;
import br.com.controlefinanceiro.model.BasCompetencia;
import br.com.controlefinanceiro.model.BasConta;
import br.com.controlefinanceiro.model.BasMovimentacao;
import br.com.controlefinanceiro.model.BasMovimentacaoFinal;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.repository.BancoContaRepository;
import br.com.controlefinanceiro.repository.BasCompetenciaRespository;
import br.com.controlefinanceiro.repository.BasMovimentacaoFinalRepository;
import br.com.controlefinanceiro.repository.ContaBaseRepository;
import br.com.controlefinanceiro.repository.LancamentoRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class MovimentaocaFinalService {

    //private final TransferenciaService transferenciaService;
    

    private BasMovimentacaoFinalRepository movimentacaoFinalRepository;
    private BasCompetenciaRespository competenciaRespository;
    private LancamentoRepository lancamentoRepository;
    private BancoContaRepository bancoContaRepository;



    public MovimentaocaFinalService(BasMovimentacaoFinalRepository movimentacaoFinalRepository, 
        BasCompetenciaRespository competenciaRespository, 
        LancamentoRepository lancamentoRepository,
    BancoContaRepository bancoContaRepository) {
        this.movimentacaoFinalRepository = movimentacaoFinalRepository;
        this.competenciaRespository = competenciaRespository;
        this.lancamentoRepository = lancamentoRepository;   
        this.bancoContaRepository =bancoContaRepository;
        //this.transferenciaService = transferenciaService;
    }


    /**
     * 
     * @param bancoContaId
     * @param competenciaId
     * @return
     */
    private boolean atualizarMovimentaacaoGeral(Long bancoContaId, Long competenciaId){
        boolean continua = true;
        Optional<BasBancoConta> bancoConta = bancoContaRepository.findById(bancoContaId);
        long t = bancoConta.get().getId();
        List<BasMovimentacao> listaLancamentos = lancamentoRepository.findByContaIdOrderByDataDesc(t,competenciaId);
        BasMovimentacaoFinal movimentacaoFinal = movimentacaoFinalRepository.findByBancoContaId(bancoConta.get().getId(),competenciaId);
        
        if(movimentacaoFinal == null){
            return false; 
        }

        BigDecimal saldoInicial = movimentacaoFinal.getVlSaldoInicial();
        BigDecimal creditoTotal = BigDecimal.ZERO;
        BigDecimal debitoTotal= BigDecimal.ZERO;

        for(BasMovimentacao l : listaLancamentos){
            BigDecimal credito = l.getVlCredito();
            BigDecimal debito = l.getVlDebito();

            creditoTotal = creditoTotal.add(credito);
            debitoTotal = debitoTotal.add(debito);

            l.setVlSaldo(saldoInicial.add(creditoTotal.add(debitoTotal.negate())));
            lancamentoRepository.save(l);
        }


        movimentacaoFinal.setVlSaldoInicial(saldoInicial);
        movimentacaoFinal.setVlSaldoFinal(saldoInicial);
        movimentacaoFinal.setVlTotalCredito(creditoTotal);
        movimentacaoFinal.setVlTotalDebito(debitoTotal);

        movimentacaoFinalRepository.save(movimentacaoFinal);

        if(continua){
            atualizarMovimentaacaoGeral(bancoContaId, competenciaId+1);
        }

        return false;
    }

    /**
     * Atualizar os saldos
     * @param bancoContaId
     * @param d
     */
    public void atualizarMovimentacaoGeral(Long bancoContaId,String d){        
        Integer[] mes =  DateUtils.getDiaMesAno(d);

        BasCompetencia competencia = competenciaRespository.consultaPorMesAno(mes[1], mes[2]);
      

        atualizarMovimentaacaoGeral(bancoContaId, competencia.getId());

       
    }


    /**
     * Crie metodo para atualizar os posteriores
     */

}