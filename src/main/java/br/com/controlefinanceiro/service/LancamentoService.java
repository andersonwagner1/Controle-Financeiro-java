package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.LancamentoResponseDto;
import br.com.controlefinanceiro.dto.SaldoResponseDto;
import br.com.controlefinanceiro.dto.ContaResumoDto;
import br.com.controlefinanceiro.dto.LancamentoRequestDto;
import br.com.controlefinanceiro.model.BasBancoConta;
import br.com.controlefinanceiro.model.BasCompetencia;
import br.com.controlefinanceiro.model.BasConta;
import br.com.controlefinanceiro.model.BasInvestimento;
import br.com.controlefinanceiro.model.BasMovimentacao;
import br.com.controlefinanceiro.model.BasMovimentacaoFinal;
import br.com.controlefinanceiro.model.BasTipoMovimentacao;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.repository.BancoContaRepository;
import br.com.controlefinanceiro.repository.BancoRepository;
import br.com.controlefinanceiro.repository.BasCompetenciaRespository;
import br.com.controlefinanceiro.repository.BasMovimentacaoFinalRepository;
import br.com.controlefinanceiro.repository.ContaBaseRepository;
import br.com.controlefinanceiro.repository.InvestimentoRepository;
import br.com.controlefinanceiro.repository.LancamentoRepository;
import br.com.controlefinanceiro.repository.TipoMovimentacaoRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class LancamentoService {
    //private final FinanceiroService financeiroService;
    //private final TransferenciaService transferenciaService;

    private final ContaBaseRepository contasBaseRepository;
    private final TipoMovimentacaoRepository categoriasRepository;
    private final BancoContaRepository vinculosRepository;
    private final LancamentoRepository lancamentosRepository;
    private final BasCompetenciaRespository competenciasRepository;
    private final BasMovimentacaoFinalRepository movimentacaoFinalRepostory;

    public LancamentoService(BancoRepository bancos, ContaBaseRepository contasBase,
            TipoMovimentacaoRepository categorias, 
            BancoContaRepository vinculos, 
            InvestimentoRepository investimentos,
            LancamentoRepository lancamentos,
            BasMovimentacaoFinalRepository movimentacaoFinalRepostory,
        BasCompetenciaRespository competencias) {
   
        this.contasBaseRepository = contasBase;
        this.categoriasRepository = categorias;
        this.vinculosRepository = vinculos;
        this.lancamentosRepository = lancamentos;
        this.competenciasRepository = competencias;
        this.movimentacaoFinalRepostory = movimentacaoFinalRepostory;
    }
    
    /**
    * Realiza a correção dos registros
    * @param dto
    */
    public void realizarLancamento(LancamentoRequestDto dto) {
        BasMovimentacao movimentacao = toEntity(dto);
        movimentacao = lancamentosRepository.save(movimentacao); 
        atualizarSaldoMovimentacaoFinal(movimentacao.getBancoConta() , movimentacao.getCompetencia());
    }


    /**
     * Realizar a consulta dos dados
     * @param contaId
     * @param dataInicial
     * @param dataFinal
     * @return
     */
    public List<LancamentoResponseDto> listar(Long contaId, Date dataInicial, Date dataFinal) {
        List<BasMovimentacao> resultado = null;
        if(contaId == null){
            resultado = lancamentosRepository.buscarPorFiltrosTodasBancos(dataInicial, dataFinal);
        }else{
            resultado = lancamentosRepository.buscarPorFiltros(contaId, dataInicial, dataFinal);
        }
        return resultado.stream().map(this::toDto).toList();
    }


    /**
     * Consultar um registro por ID
     * @param id
     * @return
     */
    public LancamentoResponseDto consultarRegistros(Long id) {
        return toDto(buscarLancamentoEntity(id));
    }


    public LancamentoResponseDto atualizar(Long id, LancamentoRequestDto dto) {
        return atualizarLancamento(id, dto);
    }

    public void excluir(Long id) {
        BasMovimentacao lancamento = buscarLancamentoEntity(id);
        lancamentosRepository.delete(lancamento);
        
        atualizarSaldoMovimentacaoFinal(lancamento.getBancoConta(), lancamento.getCompetencia());
    }




    /**
     * Consultar Saldo para mostrar no cards
     * @param contaId
     * @param dInicial
     * @return
     */
    public SaldoResponseDto consultarSaldo(Long contaId, Integer[] dInicial) {       
       return consultarDetalhesDosSaldos(contaId, dInicial);
    }


    /**
     * MAPPEADOR
     * 
     * 
     */

    private BasMovimentacao toEntity(LancamentoRequestDto dto) {
        BasMovimentacao basMovimentacao = new BasMovimentacao();
        basMovimentacao.setId(dto.id());
        basMovimentacao.setBancoConta(vinculosRepository.findById(dto.bancoContaId()).get());
        basMovimentacao.setDsObservacao(dto.observacao());
        
        BasTipoMovimentacao tipoMovimentacao = categoriasRepository.findById(dto.tipoMovimentacaoId()).get();        
        basMovimentacao.setTipoMovimentacao(tipoMovimentacao);
        basMovimentacao.setVlCredito(tipoMovimentacao.getIcTipoMovimentacao() == EnumTipoMovimentacao.CREDITO  ? dto.valor() : BigDecimal.ZERO);
        basMovimentacao.setVlDebito(tipoMovimentacao.getIcTipoMovimentacao() == EnumTipoMovimentacao.DEBITO ? dto.valor() : BigDecimal.ZERO);
        basMovimentacao.setDtMovimentacao(DateUtils.stringToDate(dto.data()));
        basMovimentacao.setDsObservacao(dto.observacao());
        //basMovimentacao.setVlSaldo(dto.saldoApos());
        
        Integer competencia[] = DateUtils.getDiaMesEAno(basMovimentacao.getDtMovimentacao());
        basMovimentacao.setNrDia(competencia[0]);
        basMovimentacao.setCompetencia(competenciasRepository.consultaPorMesAno(competencia[1], competencia[2]));
        basMovimentacao.setIcCalcular(EnumSimNao.SIM);
        basMovimentacao.setIcSituacao(EnumSimNao.SIM);

        //VERUFUCA A TABSFEREBCUA
        if(dto.transferenciaId() == null){
            return basMovimentacao;
        }
        

        //Verificar se existe transferencia
        Optional<BasMovimentacao> optino = lancamentosRepository.findById(dto.transferenciaId());

        if(optino.isEmpty()){
            basMovimentacao.setVinculado(null);
        }else{
            basMovimentacao.setVinculado(optino.get());
        }
       
        return basMovimentacao;
    }


    @Transactional
    public LancamentoResponseDto atualizarLancamento(Long id, LancamentoRequestDto dto) {
        BasMovimentacao novo = toEntity(dto);
        novo.setId(id);
        BasMovimentacao movimentacao = lancamentosRepository.save(novo);

        atualizarSaldoMovimentacaoFinal(movimentacao.getBancoConta() , movimentacao.getCompetencia());

        BasMovimentacao movimentacoo = buscarLancamentoEntity(id);
        return toDto(movimentacoo);
    }

    private LancamentoResponseDto toDto(BasMovimentacao e) {

        BasBancoConta bancoConta = Optional.ofNullable(e.getBancoConta()).orElse(null);

        BigDecimal valor = e.getVlCredito().compareTo(BigDecimal.ZERO) != 0
                    ? e.getVlCredito()
                    : e.getVlDebito().negate();

        Long investimentoId =  Optional.ofNullable(e.getInvestimento())
                .map(BasInvestimento::getId)
                .orElse(null);   
                
        LancamentoResponseDto response =  LancamentoResponseDto.builder()
            .id(e.getId())
            .bancoContaId(bancoConta.getId())
            .tipoMovimentaocaoId(e.getTipoMovimentacao().getId())
            .transferenciaId(investimentoId)
            .data(DateUtils.dateToString(e.getDtMovimentacao()))
            .tipo(e.getTipoMovimentacao().getIcTipoMovimentacao())
            .conta(bancoConta.getConta().getDsConta())
            .banco(bancoConta.getBanco().getDsBanco())
            .tipoMovimentacao(e.getTipoMovimentacao().getDsTipoMovimentacao())
            .observacao(e.getDsObservacao())
            .valor(valor)
            .saldo(e.getVlSaldo())
            .build();

            return response;
       
    }

  

    //METODOS PRIVADOS


    private SaldoResponseDto toDto(BasMovimentacaoFinal movimentacaoFinal){
        if(movimentacaoFinal == null){
            return SaldoResponseDto.builder().build();
        }

        SaldoResponseDto dto =  SaldoResponseDto.builder()
        .saldoFinal(movimentacaoFinal.getVlSaldoFinal())
        .saldoInicial(movimentacaoFinal.getVlSaldoInicial())
        .totalCredito(movimentacaoFinal.getVlTotalCredito())
        .totalDebito(movimentacaoFinal.getVlTotalDebito())
        .totalRegistro(0)
        .build();
        return dto;
    }
        public SaldoResponseDto consultarDetalhesDosSaldos(Long contaId, Integer[] data) {           
            BasCompetencia comeptencia = competenciasRepository.consultaPorMesAno(data[1], data[2]);
            
            if(comeptencia == null){
                return SaldoResponseDto.builder().build();
            }
        
            if(contaId != null){
                BasMovimentacaoFinal movimentacao = movimentacaoFinalRepostory.findByBancoContaId(contaId, comeptencia.getId());
                return toDto(movimentacao);
            }else{
                List<BasMovimentacaoFinal> listar = movimentacaoFinalRepostory.findMovimentacaoFinal(comeptencia.getId());

                if(listar.size() == 0){
                    return SaldoResponseDto.builder().build();
                }
            

                BigDecimal saldoInicial = listar.stream()
                    .map(e -> e.getVlSaldoInicial() != null ? e.getVlSaldoInicial() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal saldoFinal = listar.stream()
                        .map(e -> e.getVlSaldoFinal() != null ? e.getVlSaldoFinal() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalCredito = listar.stream()
                    .map(e -> e.getVlTotalCredito() != null ? e.getVlTotalCredito() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalDebito = listar.stream()
                        .map(e -> e.getVlTotalDebito() != null ? e.getVlTotalDebito() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            
                        BasMovimentacaoFinal movimentacaoFinal = new BasMovimentacaoFinal();
                        movimentacaoFinal.setBancoConta(listar.get(0).getBancoConta());
                        movimentacaoFinal.setCompetencia(comeptencia);
                        movimentacaoFinal.setVlSaldoFinal(saldoFinal);
                        movimentacaoFinal.setVlSaldoInicial(saldoInicial);
                        movimentacaoFinal.setVlTotalCredito(totalCredito);
                        movimentacaoFinal.setVlTotalDebito(totalDebito);
            return toDto(movimentacaoFinal);                
        }
    }

     private BasMovimentacao buscarLancamentoEntity(Long id) {
        return lancamentosRepository.findById(id).orElse(null);
    }

     /**
     * Atualizar os saldos
     * @param basBancoConta
     * @param basCompetencia
     */
     public void atualizarSaldoMovimentacaoFinal(BasBancoConta basBancoConta, BasCompetencia basCompetencia) {

        //Criar ou consultar a movimentacao Final
        BasMovimentacaoFinal movimentacaoFinal = consultarOuCriarMovimentacaoFinal(basBancoConta, basCompetencia);

        //Atualizar o saldo
        BasMovimentacaoFinal movimentacaoResultaodFinal = atualizarSaldoMovimentacao(movimentacaoFinal.getVlSaldoInicial(), basBancoConta, basCompetencia);

        //atualizar movimentacao Final
        atualizarMovimentaocaFinalAtualizarVinculo(basBancoConta, movimentacaoFinal, movimentacaoResultaodFinal);
    }


    public void atualizarSaldoMovimentacaoFinal(Long basBancoConta, String data) {
         BasBancoConta bancoConta = vinculosRepository.findById(basBancoConta).get();
        Integer[] mesDiaAno = DateUtils.getDiaMesAno(data);
        BasCompetencia competencia = competenciasRepository.consultaPorMesAno(mesDiaAno[1], mesDiaAno[2]);
        atualizarSaldoMovimentacaoFinal(bancoConta, competencia);
    }

    /**
     * 
     * @param basBancoConta
     * @param movimentacaoFinal
     * @param movimentacaoResultaodFinal
     */
    private void atualizarMovimentaocaFinalAtualizarVinculo(BasBancoConta basBancoConta, BasMovimentacaoFinal movimentacaoFinal, BasMovimentacaoFinal movimentacaoResultaodFinal){

          //atualizar movimentacao Final
        movimentacaoFinal.setVlTotalCredito(movimentacaoResultaodFinal.getVlTotalCredito());
        movimentacaoFinal.setVlTotalDebito(movimentacaoResultaodFinal.getVlTotalDebito());
        movimentacaoFinal.setVlSaldoFinal(movimentacaoResultaodFinal.getVlSaldoFinal());
        basBancoConta.setVlSaldoAtual(movimentacaoResultaodFinal.getVlSaldoFinal());
        
        //Salvar
        movimentacaoFinalRepostory.save(movimentacaoFinal);
        vinculosRepository.save(basBancoConta);
    }

    private BasMovimentacaoFinal atualizarSaldoMovimentacao(BigDecimal vlSaldoInicial, BasBancoConta basBancoConta, BasCompetencia basCompetencia){

         //listar todas as mvomentacoes
        List<BasMovimentacao> listarMovimentacao = lancamentosRepository.findByContaIdOrderByDataDesc(basBancoConta.getId(), basCompetencia.getId());

        BigDecimal inicial = vlSaldoInicial;
        BigDecimal vlSaldo = inicial;

        BigDecimal vlTotalDebito = BigDecimal.ZERO;
        BigDecimal vlTotalCredito = BigDecimal.ZERO;

        //Atualizar os saldos
        for(BasMovimentacao m : listarMovimentacao){
            BigDecimal vlDebito =  m.getVlDebito();
            BigDecimal vlCredito = m.getVlCredito();
            vlSaldo = vlSaldo.add(vlCredito.add(vlDebito.negate()));
            m.setVlSaldo(vlSaldo);
            lancamentosRepository.save(m);

            vlTotalDebito = vlTotalDebito.add(vlDebito);
            vlTotalCredito = vlTotalCredito.add(vlCredito);
        }

        BasMovimentacaoFinal movimentacao = new BasMovimentacaoFinal();
        movimentacao.setVlSaldoFinal(vlSaldo);
        movimentacao.setVlTotalCredito(vlTotalCredito);
        movimentacao.setVlTotalDebito(vlTotalDebito);


        return movimentacao;

    }



    
    private BasMovimentacaoFinal consultarOuCriarMovimentacaoFinal(BasBancoConta bancoConta, BasCompetencia competencia){
        BasMovimentacaoFinal movimentacoaFinal = movimentacaoFinalRepostory.findByBancoContaId(bancoConta.getId(),competencia.getId());

        if(movimentacoaFinal == null){
            movimentacoaFinal = new BasMovimentacaoFinal();
            movimentacoaFinal.setBancoConta(bancoConta);
            movimentacoaFinal.setCompetencia(competencia);
            BasMovimentacaoFinal movimentacoaFinalAnterior = movimentacaoFinalRepostory.findByBancoContaId(bancoConta.getId(),competencia.getId()-1);
            if(movimentacoaFinalAnterior != null){
               movimentacoaFinal.setVlSaldoInicial(movimentacoaFinalAnterior.getVlSaldoFinal());
            }

            movimentacoaFinal = movimentacaoFinalRepostory.save(movimentacoaFinal);
        }
        return movimentacoaFinal;
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
                .findFirst().orElse(null);
    }
}