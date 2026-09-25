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

    public BancoDto buscarBanco(Long id) {
        return toDto(bancoRepsoitory.findById(id).orElseThrow(() -> naoEncontrado("Banco")));
    }

    public BancoDto salvarBanco(BancoDto dto) {
        return toDto(bancoRepsoitory.save(toEntity(dto)));
    }

    public List<ContaBaseDto> listarContasBase() {
        return contasBaseRepository.findAll().stream().map(this::toDto).toList();
    }

    public ContaBaseDto buscarContaBase(Long id) {
        return toDto(contasBaseRepository.findById(id).orElseThrow(() -> naoEncontrado("Conta base")));
    }

    public ContaBaseDto salvarContaBase(ContaBaseDto dto) {
        return toDto(contasBaseRepository.save(toEntity(dto)));
    }

    public List<TipoMovimentacaoDto> listarCategorias() {
        return categoriasRepository.findAll().stream().map(this::toDto).toList();
    }

    private TipoMovimentacaoDto buscarCategoria(Long id) {
        return toDto(categoriasRepository.findById(id).orElseThrow(() -> naoEncontrado("Categoria")));
    }

    public TipoMovimentacaoDto salvarCategoria(TipoMovimentacaoDto dto) {
        return toDto(categoriasRepository.save(toEntity(dto)));
    }

    public TipoMovimentacaoDto atualizarStatusCategoria(Long id, EnumSimNao ativo) {
        BasTipoMovimentacao categoria = buscarCategoriaEntity(id);
        categoria.setIcSituacao(ativo);
        return toDto(categoriasRepository.save(categoria));
    }

    public void excluirCategoria(Long id) {
        categoriasRepository.delete(buscarCategoriaEntity(id));
    }

    public List<VinculoDto> listarVinculos() {
        return vinculosRepository.findAll().stream().map(this::toDto).toList();
    }

    public VinculoDto buscarVinculo(Long id) {
        return toDto(buscarVinculoEntity(id));
    }

    public VinculoDto salvarVinculo(VinculoDto dto) {
        return toDto(vinculosRepository.save(toEntity(dto)));
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
        atualizaMovimentacaoFinal(dto.data(), dto.bancoContaId(), dto.valor(), dto.tipo());
        
        BasMovimentacao movimentacao = toEntity(dto);
        //Registra o lancamento
        LancamentoDto to =  toDto(lancamentosRepository.save(movimentacao));

        //atualiza o saldo dos registro
        atualizarSaldo(dto.bancoContaId(), DateUtils.stringToLocale(dto.data()));
        return to;
    }


    /**
     * Ajustar o valor final do saldo, alterando a tabela movimentacao vinal e vinculo do banco com a conta
     * @param data
     * @param bancoContaId
     * @param valor
     * @param tipo
     * @return
     */
    private void atualizaMovimentacaoFinal(String data, Long bancoContaId, BigDecimal valor, EnumTipoMovimentacao tipo) {
        //separa o dia mes e ano e localiza a competencia
        Integer mesAno[] = DateUtils.getDiaMesAno(data);        
        BasCompetencia comptencia = competenciasRepository.consultaPorMesAno(mesAno[1], mesAno[2]);

        //pega o saldo inicial da competencia da conta
        BasMovimentacaoFinal movimentacaoFinal = criarMOvimentacaoFinal(vinculosRepository.findById(bancoContaId).get(), comptencia);
       
         

        //verificar se esta subtraindo ou adicionando
        if( tipo == EnumTipoMovimentacao.CREDITO){
            movimentacaoFinal.setVlTotalCredito(movimentacaoFinal.getVlTotalCredito().add(valor));            
        }else{            
            movimentacaoFinal.setVlTotalDebito(movimentacaoFinal.getVlTotalDebito().add(valor));
        }


        //Realiza o saldo do reigstro final
        movimentacaoFinal.setVlSaldoFinal(
            movimentacaoFinal.getVlSaldoInicial()
        .add(movimentacaoFinal.getVlTotalCredito())
        .add(movimentacaoFinal.getVlTotalDebito().negate())
    );
    
        //atualiza o saldo final
        movimentacaoFinalRepostory.save(movimentacaoFinal);
        
        //atualiza o saldo do vinculo
        BasBancoConta bancoConta = movimentacaoFinal.getBancoConta();
        bancoConta.setVlSaldoAtual(movimentacaoFinal.getVlSaldoFinal());
        vinculosRepository.save(bancoConta);

    }



    @Transactional
    public LancamentoDto atualizarLancamento(Long id, LancamentoDto dto) {
        BasMovimentacao antigo = buscarLancamentoEntity(id);
       // ajustarSaldo(antigo.getBancoConta().getId(), antigo.getTipoMovimentacao().getIcTipoMovimentacao(), antigo.getVlDebito().negate());
        BasMovimentacao novo = toEntity(dto);
        novo.setId(id);
       // ajustarSaldo(novo.getBancoConta().getId(), novo.getTipoMovimentacao().getIcTipoMovimentacao(), novo.getVlCredito());
        dto =  toDto(lancamentosRepository.save(novo));
        
        //atualiza o saldo dos registro
        atualizarSaldo(dto.bancoContaId(), DateUtils.stringToLocale(dto.data()));

        return dto;

    }

    @Transactional
    public void excluirLancamento(Long id) {
        BasMovimentacao lancamento = buscarLancamentoEntity(id);
       /*  ajustarSaldo(
            lancamento.getBancoConta().getId(), 
            lancamento.getTipoMovimentacao().getIcTipoMovimentacao(), 
            Optional.ofNullable(lancamento.getVlCredito()).orElse(lancamento.getVlDebito()));
*/
            lancamentosRepository.delete(lancamento);


      //ajustarSaldoFinal(lancamento.getDtMovimentacao(), lancamento.getBancoConta().getId(), lancamento.getv);      
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

    private BasConta toEntity(ContaBaseDto dto) {
        BasConta e = new BasConta();
        e.setId(dto.id());
        e.setDsConta(dto.descricao());
        e.setIcTipo(dto.tipo());
        return e;
    }

    private ContaBaseDto toDto(BasConta e) {
        return new ContaBaseDto(e.getId(), e.getDsConta(), e.getIcTipo());
    }

    private BasTipoMovimentacao toEntity(TipoMovimentacaoDto dto) {
        BasTipoMovimentacao e = new BasTipoMovimentacao();
        e.setId(dto.id());
        e.setDsTipoMovimentacao(dto.nome());
        e.setIcTipoMovimentacao(dto.tipo());
        e.setIcSituacao(dto.ativo());
        return e;
    }

    private TipoMovimentacaoDto toDto(BasTipoMovimentacao e) {
        return new TipoMovimentacaoDto(e.getId(), e.getDsTipoMovimentacao(), e.getIcTipoMovimentacao(), e.getIcSituacao());
    }

    private BasBancoConta toEntity(VinculoDto dto) {
        Integer dataInicial[] =  DateUtils.getMesEAno(dto.dataInicio());
        Integer dataFinal[] =  DateUtils.getMesEAno(dto.dataFim());

        BasBancoConta e = new BasBancoConta();
        e.setId(dto.id());
        e.setBanco(bancoRepsoitory.findById(dto.bancoId()).get());
        e.setConta(contasBaseRepository.findById(dto.contaBaseId()).get());
        e.setVlSaldoAtual(dto.saldo() == null ? BigDecimal.ZERO : dto.saldo());
        e.setDtAbertura(competenciasRepository.consultaPorMesAno(dataInicial[0], dataInicial[1]));
        e.setDtFechamento(competenciasRepository.consultaPorMesAno(dataFinal[0], dataInicial[1]));
        
        //e.setRentabilidade(dto.rentabilidade());
        //e.setVencimento(dto.vencimento());
        //e.setIcSituacao(dto.ativa() == null || dto.ativa());
        return e;
    }

    private VinculoDto toDto(BasBancoConta e) {
        return new VinculoDto(
            e.getId(), 
            e.getBanco().getId(), 
            e.getConta().getId(), 
            e.getVlSaldoAtual(), 
            DateUtils.getPrimeiroDiaDoMes(e.getDtAbertura().getDsMesAno()),
            DateUtils.getPrimeiroDiaDoMes(e.getDtFechamento().getDsMesAno()),
            null,
            DateUtils.getPrimeiroDiaDoMes(e.getDtFechamento().getDsMesAno()),
            true);
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


    private BasTipoMovimentacao buscarCategoriaEntity(Long id) {
        return categoriasRepository.findById(id).orElseThrow(() -> naoEncontrado("Tipo de Movimentacao"));
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

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }


    public List<Object[]> registrarLancamentos(Integer ano) {
    List<Object[]> listar = lancamentosRepository.registrar(ano);        
        return listar;
    }



    public void atualizarSaldo(Long contaBancoId, LocalDate competenciaId) {
        Integer[] diaMesAno = DateUtils.getDiaMesEAno(competenciaId);
        BasCompetencia basCompetencia = competenciasRepository.consultaPorMesAno(diaMesAno[1], diaMesAno[2]);
        BasBancoConta basBancoConta = vinculosRepository.findById(contaBancoId).get();
        //BasMovimentacaoFinal movimentacaoFinal =  movimentacaoFinalRepostory.findByBancoContaId(contaBancoId, diaMesAno[1], diaMesAno[2]);

        BasMovimentacaoFinal movimentacaoFinal = criarMOvimentacaoFinal(basBancoConta, basCompetencia);


        List<BasMovimentacao> listarMovimentacao = lancamentosRepository.findByContaIdOrderByDataDesc(basBancoConta.getId(), basCompetencia.getId());

        BigDecimal inicial = movimentacaoFinal.getVlSaldoInicial();
        BigDecimal vlSaldo = inicial; 
        for(BasMovimentacao m : listarMovimentacao){
            BigDecimal vlDebito =  m.getVlDebito();
            BigDecimal vlCredito = m.getVlCredito();
            vlSaldo = vlSaldo.add(inicial.add(vlCredito.add(vlDebito.negate())));
            m.setVlSaldo(vlSaldo);
            lancamentosRepository.save(m);
        }

        basBancoConta.setVlSaldoAtual(vlSaldo);
        vinculosRepository.save(basBancoConta);


    }
}
