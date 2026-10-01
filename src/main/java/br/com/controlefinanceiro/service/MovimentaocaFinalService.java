package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.model.BasBancoConta;
import br.com.controlefinanceiro.model.BasCompetencia;
import br.com.controlefinanceiro.model.BasMovimentacao;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.repository.BancoContaRepository;
import br.com.controlefinanceiro.repository.BasCompetenciaRespository;
import br.com.controlefinanceiro.repository.BasMovimentacaoFinalRepository;
import br.com.controlefinanceiro.repository.LancamentoRepository;
import br.com.controlefinanceiro.repository.TipoMovimentacaoRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MovimentaocaFinalService {

    // private final TransferenciaService transferenciaService;
    private BasCompetenciaRespository competenciaRespository;
    private LancamentoRepository lancamentoRepository;
    private BancoContaRepository bancoContaRepository;
    private LancamentoService lancamentoService;

    public MovimentaocaFinalService(           
            BasMovimentacaoFinalRepository movimentacaoFinalRepository,
            BasCompetenciaRespository competenciaRespository,
            LancamentoRepository lancamentoRepository,
            BancoContaRepository bancoContaRepository,
            LancamentoRepository movimentacaoRepository,
            TipoMovimentacaoRepository tipoMovimentacaoRepository) {
     
        this.competenciaRespository = competenciaRespository;
        this.lancamentoRepository = lancamentoRepository;
        this.bancoContaRepository = bancoContaRepository;
    }

  
    /**
     * Atualizar os saldos
     * 
     * @param bancoContaId
     * @param d
     */
    public void atualizarCorrrigirSaldoApartirDoMes(Long bancoContaId, Long competenciaFinal) {
        BasBancoConta bancoConta = bancoContaRepository.findById(bancoContaId).get();
        BasCompetencia competencia= competenciaRespository.findById(competenciaFinal).get();;

        do{
            competenciaFinal++;
            lancamentoService.atualizarSaldoMovimentacaoFinal(bancoConta, competencia);
            competencia= competenciaRespository.findById(competenciaFinal).get();
        }while (competencia != null);
    }


    public void atualizarSaldo(Long bancoContaId, String dataInicial, Double saldo) {
      
        Integer[] anomesDia = DateUtils.getDiaMesAno(dataInicial);
        BasCompetencia competencia = competenciaRespository.consultaPorMesAno(anomesDia[1], anomesDia[2]);
        BasBancoConta bancoConta = bancoContaRepository.findById(bancoContaId).get();

        List<BasMovimentacao> rendimentos = lancamentoRepository.listarRendimentos(bancoContaId, competencia.getId() );

        BasMovimentacao registroLencimento  = null;
        
        //zerar o registros
        if(rendimentos.size() != 0){
            registroLencimento = rendimentos.get(0);
            registroLencimento.setVlDebito(BigDecimal.ZERO);
            registroLencimento.setVlCredito(BigDecimal.ZERO);
            lancamentoRepository.save(registroLencimento);
            lancamentoService.atualizarSaldoMovimentacaoFinal(bancoConta, competencia);
        }else{
            registroLencimento = new BasMovimentacao();
        }
    }
}
