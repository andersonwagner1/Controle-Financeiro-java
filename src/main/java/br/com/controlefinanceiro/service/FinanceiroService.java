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

    public List<LancamentoDto> listarLancamentos(Long contaId, Date dataInicial, Date dataFinal) {
        List<BasMovimentacao> resultado = null;
        if(contaId == null){
            resultado = lancamentosRepository.buscarPorFiltrosTodasBancos(dataInicial, dataFinal);
        }else{
            resultado = lancamentosRepository.buscarPorFiltros(contaId, dataInicial, dataFinal);
        }

        
        return resultado.stream().map(this::toDto).toList();
    }

    public LancamentoDto buscarLancamento(Long id) {
        return toDto(buscarLancamentoEntity(id));
    }

    @Transactional
    public LancamentoDto criarLancamento(LancamentoDto dto) {
        BasMovimentacao movimentacao = toEntity(dto);
        //Registra o lancamento
        LancamentoDto to =  toDto(lancamentosRepository.save(movimentacao));

        //atualiza o saldo dos registro
        atualizarSaldoMovimentacoaFinal(dto.bancoContaId(), DateUtils.stringToLocale(dto.data()));
        return to;
    }


    @Transactional
    public LancamentoDto atualizarLancamento(Long id, LancamentoDto dto) {
        //BasMovimentacao antigo = buscarLancamentoEntity(id);
       // ajustarSaldo(antigo.getBancoConta().getId(), antigo.getTipoMovimentacao().getIcTipoMovimentacao(), antigo.getVlDebito().negate());
        BasMovimentacao novo = toEntity(dto);
        novo.setId(id);
       // ajustarSaldo(novo.getBancoConta().getId(), novo.getTipoMovimentacao().getIcTipoMovimentacao(), novo.getVlCredito());
        dto =  toDto(lancamentosRepository.save(novo));
        
        //atualiza o saldo dos registro
        atualizarSaldoMovimentacoaFinal(dto.bancoContaId(), DateUtils.stringToLocale(dto.data()));

        return dto;

    }

    @Transactional
    public void excluirLancamento(Long id) {
        BasMovimentacao lancamento = buscarLancamentoEntity(id);
        lancamentosRepository.delete(lancamento);
        atualizarSaldoMovimentacoaFinal(lancamento.getBancoConta().getId(), DateUtils.toLocalDate(lancamento.getDtMovimentacao()));
    }

    @Transactional
    public void transferir(TransferenciaDto request) {
      
        BasMovimentacao movimentacaoOrigem = novoLancamento(
            request.bancoContaId(), 
            request.data(), 
            request.tipoTransferencia(), 
            request.valor(), 
            request.investimento(), 
            true);

       movimentacaoOrigem =  lancamentosRepository.save(movimentacaoOrigem);

       BasMovimentacao movimentacaoDestino =  novoLancamento(
        request.bancoContaDestinoId(), 
       request.data(), 
       request.tipoTransferencia(), 
       request.valor(), 
       request.investimento(), false);

       movimentacaoDestino.setVinculado(movimentacaoOrigem);
       movimentacaoDestino = lancamentosRepository.save(movimentacaoDestino);
       
       
       //VINCULAR COM O DESTINO
        movimentacaoOrigem.setVinculado(movimentacaoDestino);
        lancamentosRepository.save(movimentacaoOrigem);
    }

    private BasMovimentacaoFinal criarMOvimentacaoFinal(BasBancoConta bancoConta, BasCompetencia competencia){
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


    private BasMovimentacao novoLancamento(Long bancoContaId, LocalDate data, String tipoMovimentacao, BigDecimal valor, Long investimentoId, boolean credito) {
        BasBancoConta bancoConta = vinculosRepository.findById(bancoContaId).orElseThrow(() -> new IllegalArgumentException("Conta bancária não encontrada para o ID: " + bancoContaId));

        BasCompetencia competencia = competenciasRepository.consultaPorMesAno(data.getMonthValue(), data.getYear());
        BasMovimentacaoFinal movimentacoaFinal= criarMOvimentacaoFinal(bancoConta, competencia);

        BasMovimentacao movimentacao = new BasMovimentacao();
        movimentacao.setBancoConta(bancoConta);
        movimentacao.setCompetencia(competencia);
        movimentacao.setDtMovimentacao(DateUtils.toDate(data));
        movimentacao.setIcCalcular(EnumSimNao.SIM);
        movimentacao.setIcSituacao(EnumSimNao.SIM);
        movimentacao.setNrDia(data.getDayOfMonth());
        

        // Define se o lançamento será de Débito com base na regra de negócio
        boolean eAplicacao = "APLICACAO".equals(tipoMovimentacao);
        boolean eResgate = "RESGATE".equals(tipoMovimentacao);
        boolean eTransferencia = "TRANSFERENCIA".equals(tipoMovimentacao);

        BasTipoMovimentacao tipoMOvimentacaoBas = null;
        
        
        if(eAplicacao){
            tipoMOvimentacaoBas = categoriasRepository.findById(8l).orElseThrow(() -> naoEncontrado("Categoria"));

            if(credito){
                movimentacao.setDsObservacao("Aplicação enviada");
                movimentacao.setVlDebito(valor);
                movimentacao.setVlCredito(BigDecimal.ZERO);
                movimentacoaFinal.setVlTotalDebito(movimentacoaFinal.getVlTotalDebito().add(valor));
            }else{
                movimentacao.setDsObservacao("Aplicação recebida");
                movimentacao.setVlCredito(valor);
                movimentacao.setVlDebito(BigDecimal.ZERO);
                movimentacoaFinal.setVlTotalCredito(movimentacoaFinal.getVlTotalCredito().add(valor));

            }
        }


        if(eResgate){
            tipoMOvimentacaoBas = categoriasRepository.findById(2l).orElseThrow(() -> naoEncontrado("Categoria"));  
             
            if(credito){
                movimentacao.setDsObservacao("Resgate recebido");
                movimentacao.setVlCredito(valor);
                movimentacao.setVlDebito(BigDecimal.ZERO);
                movimentacoaFinal.setVlTotalCredito(movimentacoaFinal.getVlTotalCredito().add(valor));
            }else{
                movimentacao.setDsObservacao("Resgate enviada");
                movimentacao.setVlDebito(valor);
                movimentacao.setVlCredito(BigDecimal.ZERO);
                movimentacoaFinal.setVlTotalDebito(movimentacoaFinal.getVlTotalDebito().add(valor));
            }
        }

        if(eTransferencia){
            tipoMOvimentacaoBas = categoriasRepository.findById(27L).orElseThrow(() -> naoEncontrado("Categoria")); 

            if(credito){
                movimentacao.setDsObservacao("Transferencia enviada");
                movimentacao.setVlDebito(valor);  
                movimentacao.setVlCredito(BigDecimal.ZERO);
                              
                movimentacoaFinal.setVlTotalCredito(movimentacoaFinal.getVlTotalCredito().add(valor));
            }else{
                movimentacao.setDsObservacao("Transferencia recebido");
                movimentacao.setVlCredito(valor);
                movimentacao.setVlDebito(BigDecimal.ZERO);
                movimentacoaFinal.setVlTotalDebito(movimentacoaFinal.getVlTotalDebito().add(valor));
            }
        }

        movimentacao.setTipoMovimentacao(tipoMOvimentacaoBas);

        //Tipo de investimentos
        if(eAplicacao || eResgate){
            Optional<BasInvestimento> tipoInvestimento = investimentosRepository.findById(investimentoId);            
            movimentacao.setInvestimento(tipoInvestimento.get());
        }else{
            movimentacao.setInvestimento(null);
        }

        BigDecimal saldo = Optional.ofNullable(movimentacao.getVlCredito()).map(value -> value).orElse(BigDecimal.ZERO);
        BigDecimal vlDebito = Optional.ofNullable(movimentacao.getVlDebito()).orElse(BigDecimal.ZERO);
        BigDecimal vlCredito = Optional.ofNullable(movimentacao.getVlCredito()).orElse(BigDecimal.ZERO);
        BigDecimal vlInicial = Optional.ofNullable(movimentacoaFinal.getVlSaldoInicial()).orElse(BigDecimal.ZERO);

            saldo = 
                vlCredito
                .add(vlDebito.negate())
                .add(vlInicial);
                
            
            movimentacoaFinal.setVlSaldoFinal(saldo);
            movimentacaoFinalRepostory.save(movimentacoaFinal);

       return  movimentacao;        
    }



    private BasBanco toEntity(BancoDto dto) {
        BasBanco e = new BasBanco();
        e.setId(dto.id());
        e.setDsBanco(dto.nome());
        e.setLogo(dto.logo());
        e.setCor(dto.cor());
        e.setCorSecundaria(dto.corSecundaria());
        return e;
    }

    private BancoDto toDto(BasBanco e) {
        return new BancoDto(e.getId(), e.getDsBanco(), e.getLogo(), e.getCor(), e.getCorSecundaria());
    }


    private BasMovimentacao toEntity(LancamentoDto dto) {
        BasMovimentacao basMovimentacao = new BasMovimentacao();
        basMovimentacao.setId(dto.id());
        basMovimentacao.setBancoConta(vinculosRepository.findById(dto.bancoContaId()).get());
        basMovimentacao.setDsObservacao(dto.descricao());
        
        BasTipoMovimentacao tipoMovimentacao = categoriasRepository.consultarPorNome(dto.categoria());        
        basMovimentacao.setTipoMovimentacao(tipoMovimentacao);
        basMovimentacao.setVlCredito(dto.tipo() == EnumTipoMovimentacao.CREDITO  ? dto.valor() : BigDecimal.ZERO);
        basMovimentacao.setVlDebito(dto.tipo() == EnumTipoMovimentacao.DEBITO ? dto.valor() : BigDecimal.ZERO);
        basMovimentacao.setDtMovimentacao(DateUtils.stringToDate(dto.data()));
        basMovimentacao.setDsObservacao(dto.observacao());
        basMovimentacao.setVlSaldo(dto.saldoApos());
        
        Integer competencia[] = DateUtils.getDiaMesEAno(basMovimentacao.getDtMovimentacao());
        basMovimentacao.setNrDia(competencia[0]);
        basMovimentacao.setCompetencia(competenciasRepository.consultaPorMesAno(competencia[1], competencia[2]));
        basMovimentacao.setIcCalcular(EnumSimNao.SIM);
        basMovimentacao.setIcSituacao(EnumSimNao.SIM);

        //VERUFUCA A TABSFEREBCUA
        if(dto.transferenciaId() == null){
            return basMovimentacao;
        }
        Optional<BasMovimentacao> optino = lancamentosRepository.findById(dto.transferenciaId());

        if(optino.isEmpty()){
            basMovimentacao.setVinculado(null);
        }else{
            basMovimentacao.setVinculado(optino.get());
        }
       
        return basMovimentacao;
    }

    private LancamentoDto toDto(BasMovimentacao e) {

        Long t = Optional.ofNullable(e.getVinculado())
                 .map(BasMovimentacao::getId) // Substitua 'Vinculado' pela classe do objeto retornado por getVinculado()
                 .orElse(null);

                 BigDecimal valor = e.getVlCredito().compareTo(BigDecimal.ZERO) != 0
                    ? e.getVlCredito()
                    : e.getVlDebito().negate();
                
               

        return new LancamentoDto(e.getId(), e.getBancoConta().getId(),
        e.getTipoMovimentacao().getIcTipoMovimentacao(), e.getDsObservacao()
        , e.getTipoMovimentacao().getDsTipoMovimentacao(),  valor, 
         DateUtils.dateToString(e.getDtMovimentacao()), e.getDsObservacao(),  e.getVlSaldo(), 
        Optional.ofNullable(e.getInvestimento())
                .map(BasInvestimento::getId)
                .orElse(null)
                 );
    }


    private BasBancoConta buscarVinculoEntity(Long id) {
        return vinculosRepository.findById(id).orElseThrow(() -> naoEncontrado("Vínculo"));
    }


    private BasMovimentacao buscarLancamentoEntity(Long id) {
        return lancamentosRepository.findById(id).orElseThrow(() -> naoEncontrado("Lançamento"));
    }

    private ResponseStatusException naoEncontrado(String nome) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, nome + " não encontrado");
    }


    public void atualizarSaldoMovimentacoaFinal(Long contaBancoId, LocalDate competenciaId) {
        Integer[] diaMesAno = DateUtils.getDiaMesEAno(competenciaId);
        BasCompetencia basCompetencia = competenciasRepository.consultaPorMesAno(diaMesAno[1], diaMesAno[2]);
        BasBancoConta basBancoConta = buscarVinculoEntity(contaBancoId);
        //BasMovimentacaoFinal movimentacaoFinal =  movimentacaoFinalRepostory.findByBancoContaId(contaBancoId, diaMesAno[1], diaMesAno[2]);

        BasMovimentacaoFinal movimentacaoFinal = criarMOvimentacaoFinal(basBancoConta, basCompetencia);


        List<BasMovimentacao> listarMovimentacao = lancamentosRepository.findByContaIdOrderByDataDesc(basBancoConta.getId(), basCompetencia.getId());

        //Atualizar o saldo
        BigDecimal inicial = movimentacaoFinal.getVlSaldoInicial();
        BigDecimal vlSaldo = inicial; 
        BigDecimal vlTotalDebito = BigDecimal.ZERO;
        BigDecimal vlTotalCredito = BigDecimal.ZERO;
        for(BasMovimentacao m : listarMovimentacao){
            BigDecimal vlDebito =  m.getVlDebito();
            BigDecimal vlCredito = m.getVlCredito();
            vlSaldo = vlSaldo.add(inicial.add(vlCredito.add(vlDebito.negate())));
            m.setVlSaldo(vlSaldo);
            lancamentosRepository.save(m);

            vlTotalDebito.add(vlDebito);
            vlTotalCredito.add(vlCredito);
        }

        //atualizar movimentacao Final
        movimentacaoFinal.setVlTotalCredito(vlTotalCredito);
        movimentacaoFinal.setVlTotalDebito(vlTotalDebito);
        movimentacaoFinal.setVlSaldoFinal(vlSaldo);
        basBancoConta.setVlSaldoAtual(vlSaldo);
        
        //Salvar
        movimentacaoFinalRepostory.save(movimentacaoFinal);
        vinculosRepository.save(basBancoConta);
    }

    

    public BasMovimentacaoFinal consultarSaldo(Long contaId, Date dInicial) {
        Integer[] data = DateUtils.getDiaMesEAno(dInicial);
        BasCompetencia comeptencia = competenciasRepository.consultaPorMesAno(data[1], data[2]);
        
        if(contaId != null){
            return  movimentacaoFinalRepostory.findByBancoContaId(contaId, comeptencia.getId());
        }else{

            List<BasMovimentacaoFinal> listar = movimentacaoFinalRepostory.findMovimentacaoFinal(comeptencia.getId());

            if(listar.size() == 0){
                return new BasMovimentacaoFinal();
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
            return movimentacaoFinal;                        
        }
    }
}

