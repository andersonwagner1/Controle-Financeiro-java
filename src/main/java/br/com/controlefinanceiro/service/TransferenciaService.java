package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.TransferenciaDto;
import br.com.controlefinanceiro.model.BasBancoConta;
import br.com.controlefinanceiro.model.BasCompetencia;
import br.com.controlefinanceiro.model.BasMovimentacao;
import br.com.controlefinanceiro.model.BasTipoMovimentacao;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import br.com.controlefinanceiro.repository.BancoContaRepository;
import br.com.controlefinanceiro.repository.BancoRepository;
import br.com.controlefinanceiro.repository.BasCompetenciaRespository;
import br.com.controlefinanceiro.repository.BasMovimentacaoFinalRepository;
import br.com.controlefinanceiro.repository.InvestimentoRepository;
import br.com.controlefinanceiro.repository.LancamentoRepository;
import br.com.controlefinanceiro.repository.TipoMovimentacaoRepository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class TransferenciaService {
    private final LancamentoService lancamentoService;
    private final LancamentoRepository lancamentoRepository;
     final TipoMovimentacaoRepository categorias;
    final        BancoContaRepository vinculos;
     final       InvestimentoRepository investimentos;
     final       BasMovimentacaoFinalRepository movimentacaoFinalRepostory;
     private BasCompetenciaRespository contasBase;

    public TransferenciaService(LancamentoService lancamentoService, 
        LancamentoRepository lancamentoRepository,
        BancoRepository bancos, BasCompetenciaRespository contasBase,
            TipoMovimentacaoRepository tipoMovimentacaoRepository, 
            BancoContaRepository vinculos, 
            InvestimentoRepository investimentos,
            BasMovimentacaoFinalRepository movimentacaoFinalRepostory) {
        this.lancamentoService = lancamentoService;
        this.lancamentoRepository = lancamentoRepository;
        this.categorias = tipoMovimentacaoRepository;
        this.investimentos = investimentos;
        this.vinculos= vinculos;
        this.movimentacaoFinalRepostory = movimentacaoFinalRepostory;
        this.contasBase= contasBase;

    }


    private BasMovimentacao registrarTransferencia(TransferenciaDto dto, boolean origem){

        Optional<BasBancoConta> bancoConta = vinculos.findById(dto.bancoContaId());
       
         if(origem){
            bancoConta = vinculos.findById(dto.bancoContaId());
        }else{
            bancoConta = vinculos.findById(dto.bancoContaDestinoId());
        }


        boolean credito = true;

        String acao = "";
        String tipo = "";

        BasTipoMovimentacao basTipoMovimentacao = null;
        if("APLICACAO".equals(dto.tipoTransferencia())){
            credito = !origem;
            acao = "Aplicacao";
            if(origem){
                tipo = " Enviada";
            }else{
               tipo = " Recebida";
            }


           basTipoMovimentacao = categorias.findById(8L).get();
        }

         if("RESGATE".equals(dto.tipoTransferencia())){
            credito = origem;
             acao = "Resgate";
             if(origem){
                tipo = " Recebido";
            }else{
               tipo = " Enviada";
            }
            basTipoMovimentacao = categorias.findById(2L).get();
        }


        if("TRANSFERENCIA".equals(dto.tipoTransferencia())){
            credito = !origem;
            acao = "Transferencia";

            if(origem){
                tipo = " Enviada";
            }else{
               tipo = " recebido";
            }

            basTipoMovimentacao = categorias.findById(27L).get();
        }

        Integer diaMesAno[] = DateUtils.getDiaMesEAno(dto.data());

        BasCompetencia basCompetencia = contasBase.consultaPorMesAno(diaMesAno[1], diaMesAno[2]);
        
        

        BasMovimentacao movimentacao = new   BasMovimentacao();
        movimentacao.setBancoConta(bancoConta.get());
        movimentacao.setCompetencia(basCompetencia);

        movimentacao.setDsObservacao(acao.concat(tipo));
        movimentacao.setDtMovimentacao(null);
        movimentacao.setNrDia(0);
        movimentacao.setNrPosicao(0);
        movimentacao.setDtMovimentacao(DateUtils.toDate(dto.data()));
        movimentacao.setTipoMovimentacao(basTipoMovimentacao);
        movimentacao.setIcCalcular(EnumSimNao.SIM);
        movimentacao.setIcSituacao(EnumSimNao.SIM);



        if(credito){
            movimentacao.setVlCredito(dto.valor());
            movimentacao.setVlDebito(BigDecimal.ZERO);            
        }else{
            movimentacao.setVlCredito(BigDecimal.ZERO);
            movimentacao.setVlDebito(dto.valor());
        }
        
         
        
        if(dto.investimento() != null){
            movimentacao.setInvestimento(investimentos.findById((dto.investimento())).get());
        }
        
        return movimentacao;
    }


    public void criar(TransferenciaDto dto) {

        BasMovimentacao movimentacaoOrigem = registrarTransferencia(dto, true);
        BasMovimentacao movimentacaoDestino = registrarTransferencia(dto, false);

        movimentacaoOrigem = lancamentoRepository.save(movimentacaoOrigem);

        movimentacaoDestino.setVinculado(movimentacaoOrigem);

        movimentacaoDestino = lancamentoRepository.save(movimentacaoDestino);

        movimentacaoOrigem.setVinculado(movimentacaoDestino);
        movimentacaoOrigem = lancamentoRepository.save(movimentacaoOrigem);


        lancamentoService.atualizarSaldoMovimentacaoFinal(movimentacaoOrigem.getBancoConta(), movimentacaoOrigem.getCompetencia());
        lancamentoService.atualizarSaldoMovimentacaoFinal(movimentacaoDestino.getBancoConta(), movimentacaoDestino.getCompetencia());
 }


}