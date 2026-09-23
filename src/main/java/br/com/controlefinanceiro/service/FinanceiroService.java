package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.*;
import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.repository.*;
import java.math.BigDecimal;
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

    public TipoMovimentacaoDto buscarCategoria(Long id) {
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
        ajustarSaldoFinal(dto.data(), dto.bancoContaId(), dto.valor(), dto.tipo());
        BasMovimentacao movimentacao = toEntity(dto);
        return toDto(lancamentosRepository.save(movimentacao));
    }


    /**
     * Ajustar o valor final do saldo, alterando a tabela movimentacao vinal e vinculo do banco com a conta
     * @param data
     * @param bancoContaId
     * @param valor
     * @param tipo
     * @return
     */
    private void ajustarSaldoFinal(String data, Long bancoContaId, BigDecimal valor, EnumTipoMovimentacao tipo) {
        Integer mesAno[] = DateUtils.getDiaMesAno(data);
        BasMovimentacaoFinal movimentacao = movimentacaoFinalRepostory.findByBancoContaId(bancoContaId, mesAno[1], mesAno[2]);
        BasCompetencia comptencia = competenciasRepository.consultaPorMesAno(mesAno[1], mesAno[2]);

        if(movimentacao == null){
            BasBancoConta bancoConta = buscarVinculoEntity(bancoContaId);
            movimentacao = new BasMovimentacaoFinal();
            movimentacao.setCompetencia(comptencia);
            movimentacao.setBancoConta(bancoConta);         
        }


        //verificar se esta subtraindo ou adicionando
        if( tipo == EnumTipoMovimentacao.CREDITO){
            movimentacao.setVlTotalCredito(movimentacao.getVlTotalCredito().add(valor));
            
        }else{            
            movimentacao.setVlTotalDebito(movimentacao.getVlTotalDebito().add(valor));
        }


        //Realiza a somatoria
        movimentacao.setVlSaldoFinal(
            movimentacao.getVlSaldoInicial()
        .add(movimentacao.getVlTotalCredito())
        .add(movimentacao.getVlTotalDebito().negate())
    );
    

        movimentacaoFinalRepostory.save(movimentacao);
        
        //
        BasBancoConta bancoConta = movimentacao.getBancoConta();
        bancoConta.setVlSaldoAtual(movimentacao.getVlSaldoFinal());
        vinculosRepository.save(bancoConta);


    }



    @Transactional
    public LancamentoDto atualizarLancamento(Long id, LancamentoDto dto) {
        BasMovimentacao antigo = buscarLancamentoEntity(id);
       // ajustarSaldo(antigo.getBancoConta().getId(), antigo.getTipoMovimentacao().getIcTipoMovimentacao(), antigo.getVlDebito().negate());
        BasMovimentacao novo = toEntity(dto);
        novo.setId(id);
       // ajustarSaldo(novo.getBancoConta().getId(), novo.getTipoMovimentacao().getIcTipoMovimentacao(), novo.getVlCredito());
        return toDto(lancamentosRepository.save(novo));
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
    public TransferenciaDto transferir(TransferenciaDto request) {
        //BigDecimal saldoOrigem = buscarSaldoTransferencia(request.contaOrigemId());
        //if (request.contaOrigemId().equals(request.contaDestinoId()))
        //    throw erro("As contas devem ser diferentes");
        //if (request.valor() == null || request.valor().signum() <= 0)d
        //    throw erro("O valor deve ser positivo");
      //  if (saldoOrigem.compareTo(request.valor()) < 0)
       //     throw erro("Saldo insuficiente");

        Long transferenciaId = request.id(); 
        lancamentosRepository.save(novoLancamento(request.contaOrigemId(),EnumTipoMovimentacao.DEBITO, "Transferência Enviada", request, transferenciaId));
        lancamentosRepository.save(novoLancamento(request.contaDestinoId(), EnumTipoMovimentacao.CREDITO, "Transferência Recebida", request, transferenciaId));
       // ajustarSaldo(request.contaOrigemId(), EnumTipoMovimentacao.DEBITO, request.valor());
       // ajustarSaldo(request.contaDestinoId(), EnumTipoMovimentacao.CREDITO, request.valor());
        return new TransferenciaDto(transferenciaId, request.contaOrigemId(), request.contaDestinoId(), request.valor(), request.data(),
                request.descricao(), request.investimentoId());
    }

    /**
     * 
     */
    private BasMovimentacao novoLancamento(Long contaId, EnumTipoMovimentacao tipo, String categoria, TransferenciaDto request, Long transferenciaId) {
        
        BasMovimentacao lancamento = new BasMovimentacao();
        lancamento.setBancoConta(vinculosRepository.findById(contaId).get());
       // lancamento.setTipo(tipo);
        lancamento.setDsObservacao(request.descricao());
        
        BasTipoMovimentacao tipoMovimentacao = categoriasRepository.consultarPorNome(categoria);        
        lancamento.setTipoMovimentacao(tipoMovimentacao);
        
        lancamento.setVlCredito(Optional.ofNullable(request.valor()).filter(v-> v.doubleValue() >0.0).orElse(null));
        lancamento.setVlDebito(Optional.ofNullable(request.valor()).filter(v-> v.doubleValue()<0.0).orElse(null));
        lancamento.setDtMovimentacao(DateUtils.toDate(request.data()));
        lancamento.setVinculado(lancamentosRepository.findById(transferenciaId).get());
        //lancamento.setInvestimentoId(transferenciaId);
        return lancamento;
    }

   

    private BigDecimal buscarSaldoTransferencia(Long contaId) {
        BasBancoConta vinculo = vinculosRepository.findById(contaId).orElse(null);
        if (vinculo != null)
            return vinculo.getVlSaldoAtual() == null ? BigDecimal.ZERO : vinculo.getVlSaldoAtual();
      //  Investimento investimento = buscarInvestimentoEntity(contaId);
        return null;
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
        basMovimentacao.setVlCredito(dto.valor().doubleValue()  > 0.0 ? dto.valor() : BigDecimal.ZERO);
        basMovimentacao.setVlDebito(dto.valor().doubleValue()  < 0.0 ? dto.valor() : BigDecimal.ZERO);
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

        return new LancamentoDto(e.getId(), 
        e.getBancoConta().getId(),
        e.getTipoMovimentacao().getIcTipoMovimentacao(), e.getDsObservacao()
        , e.getTipoMovimentacao().getDsTipoMovimentacao(), 
        Optional.ofNullable(e.getVlCredito())
            .filter(v ->  v.compareTo(BigDecimal.ZERO) != 0.0)
            .orElseGet(e::getVlDebito), 
         DateUtils.dateToString(e.getDtMovimentacao()), 
        e.getDsObservacao(), 
        e.getVlSaldo(), 
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

    private BasInvestimento buscarInvestimentoEntity(Long id) {
        return investimentosRepository.findById(id).orElseThrow(() -> naoEncontrado("Investimento"));
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
}
