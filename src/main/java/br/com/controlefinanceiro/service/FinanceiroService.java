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
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FinanceiroService {
    private final BancoRepository bancos;
    private final ContaBaseRepository contasBase;
    private final TipoMovimentacaoRepository categorias;
    private final BancoContaRepository vinculos;
    private final InvestimentoRepository investimentos;
    private final LancamentoRepository lancamentos;
    private final BasCompetenciaRespository competencias;

    public FinanceiroService(BancoRepository bancos, ContaBaseRepository contasBase,
            TipoMovimentacaoRepository categorias, 
            BancoContaRepository vinculos, 
            InvestimentoRepository investimentos,
            LancamentoRepository lancamentos,


        BasCompetenciaRespository competencias) {
        this.bancos = bancos;
        this.contasBase = contasBase;
        this.categorias = categorias;
        this.vinculos = vinculos;
        this.investimentos = investimentos;
        this.lancamentos = lancamentos;
        this.competencias = competencias;
    }


    
    public List<BancoDto> listarBancos() {
        return bancos.findAll().stream().map(this::toDto).toList();
    }

    public BancoDto buscarBanco(Long id) {
        return toDto(bancos.findById(id).orElseThrow(() -> naoEncontrado("Banco")));
    }

    public BancoDto salvarBanco(BancoDto dto) {
        return toDto(bancos.save(toEntity(dto)));
    }

    public List<ContaBaseDto> listarContasBase() {
        return contasBase.findAll().stream().map(this::toDto).toList();
    }

    public ContaBaseDto buscarContaBase(Long id) {
        return toDto(contasBase.findById(id).orElseThrow(() -> naoEncontrado("Conta base")));
    }

    public ContaBaseDto salvarContaBase(ContaBaseDto dto) {
        return toDto(contasBase.save(toEntity(dto)));
    }

    public List<TipoMovimentacaoDto> listarCategorias() {
        return categorias.findAll().stream().map(this::toDto).toList();
    }

    public TipoMovimentacaoDto buscarCategoria(Long id) {
        return toDto(categorias.findById(id).orElseThrow(() -> naoEncontrado("Categoria")));
    }

    public TipoMovimentacaoDto salvarCategoria(TipoMovimentacaoDto dto) {
        return toDto(categorias.save(toEntity(dto)));
    }

    public TipoMovimentacaoDto atualizarStatusCategoria(Long id, EnumSimNao ativo) {
        BasTipoMovimentacao categoria = buscarCategoriaEntity(id);
        categoria.setIcSituacao(ativo);
        return toDto(categorias.save(categoria));
    }

    public void excluirCategoria(Long id) {
        categorias.delete(buscarCategoriaEntity(id));
    }

    public List<VinculoDto> listarVinculos() {
        return vinculos.findAll().stream().map(this::toDto).toList();
    }

    public VinculoDto buscarVinculo(Long id) {
        return toDto(buscarVinculoEntity(id));
    }

    public VinculoDto salvarVinculo(VinculoDto dto) {
        return toDto(vinculos.save(toEntity(dto)));
    }

    public VinculoDto atualizarSaldo(Long id, BigDecimal saldo) {
        BasBancoConta vinculo = buscarVinculoEntity(id);
        vinculo.setVlSaldoAtual(saldo);
        return toDto(vinculos.save(vinculo));
    }

    public List<ContaResumoDto> listarContas() {
        return vinculos.findAll().stream().map(vinculo -> {
            BasConta base = contasBase.findById(vinculo.getConta().getId()).orElse(null);
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
            resultado = lancamentos.buscarPorFiltrosTodasBancos(dataInicial, dataFinal);
        }else{
            resultado = lancamentos.buscarPorFiltros(contaId, dataInicial, dataFinal);
        }

        
        return resultado.stream().map(this::toDto).toList();
    }

    public LancamentoDto buscarLancamento(Long id) {
        return toDto(buscarLancamentoEntity(id));
    }

    @Transactional
    public LancamentoDto criarLancamento(LancamentoDto dto) {
        ajustarSaldo(dto.bancoContaId(), dto.tipo(), dto.valor());
        return toDto(lancamentos.save(toEntity(dto)));
    }

    @Transactional
    public LancamentoDto atualizarLancamento(Long id, LancamentoDto dto) {
        BasMovimentacao antigo = buscarLancamentoEntity(id);
        ajustarSaldo(antigo.getBancoConta().getId(), antigo.getTipoMovimentacao().getIcTipoMovimentacao(), antigo.getVlDebito().negate());
        BasMovimentacao novo = toEntity(dto);
        novo.setId(id);
        ajustarSaldo(novo.getBancoConta().getId(), novo.getTipoMovimentacao().getIcTipoMovimentacao(), novo.getVlCredito());
        return toDto(lancamentos.save(novo));
    }

    @Transactional
    public void excluirLancamento(Long id) {
        BasMovimentacao lancamento = buscarLancamentoEntity(id);
        ajustarSaldo(
            lancamento.getBancoConta().getId(), 
            lancamento.getTipoMovimentacao().getIcTipoMovimentacao(), 
            Optional.ofNullable(lancamento.getVlCredito()).orElse(lancamento.getVlDebito()));

            lancamentos.delete(lancamento);
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
        lancamentos.save(novoLancamento(request.contaOrigemId(),EnumTipoMovimentacao.DEBITO, "Transferência Enviada", request, transferenciaId));
        lancamentos.save(novoLancamento(request.contaDestinoId(), EnumTipoMovimentacao.CREDITO, "Transferência Recebida", request, transferenciaId));
        ajustarSaldo(request.contaOrigemId(), EnumTipoMovimentacao.DEBITO, request.valor());
        ajustarSaldo(request.contaDestinoId(), EnumTipoMovimentacao.CREDITO, request.valor());
        return new TransferenciaDto(transferenciaId, request.contaOrigemId(), request.contaDestinoId(), request.valor(), request.data(),
                request.descricao(), request.investimentoId());
    }

    /**
     * 
     */
    private BasMovimentacao novoLancamento(Long contaId, EnumTipoMovimentacao tipo, String categoria, TransferenciaDto request, Long transferenciaId) {
        
        BasMovimentacao lancamento = new BasMovimentacao();
        lancamento.setBancoConta(vinculos.findById(contaId).get());
       // lancamento.setTipo(tipo);
        lancamento.setDsObservacao(request.descricao());
        
        BasTipoMovimentacao tipoMovimentacao = categorias.consultarPorNome(categoria);        
        lancamento.setTipoMovimentacao(tipoMovimentacao);
        
        lancamento.setVlCredito(Optional.ofNullable(request.valor()).filter(v-> v.doubleValue() >0.0).orElse(null));
        lancamento.setVlDebito(Optional.ofNullable(request.valor()).filter(v-> v.doubleValue()<0.0).orElse(null));
        lancamento.setDtMovimentacao(DateUtils.toDate(request.data()));
        lancamento.setVinculado(lancamentos.findById(transferenciaId).get());
        //lancamento.setInvestimentoId(transferenciaId);
        return lancamento;
    }

    private void ajustarSaldo(Long bancoContaId, EnumTipoMovimentacao tipo, BigDecimal valor) {
        BigDecimal delta = tipo != EnumTipoMovimentacao.CREDITO ? valor : valor.negate();
        BasBancoConta vinculo = vinculos.findById(bancoContaId).orElse(null);
        if (vinculo != null) {
            vinculo.setVlSaldoAtual(vinculo.getVlSaldoAtual() == null ? delta : vinculo.getVlSaldoAtual().add(delta));
            vinculos.save(vinculo);
            return;
        }
       
       //CORRIGIR ESTA LINHA
        //BasInvestimento investimento = buscarInvestimentoEntity(bancoContaId);        
        //investimentos.save(investimento);
    }

    private BigDecimal buscarSaldoTransferencia(Long contaId) {
        BasBancoConta vinculo = vinculos.findById(contaId).orElse(null);
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
        e.setBanco(bancos.findById(dto.bancoId()).get());
        e.setConta(contasBase.findById(dto.contaBaseId()).get());
        e.setVlSaldoAtual(dto.saldo() == null ? BigDecimal.ZERO : dto.saldo());
        e.setDtAbertura(competencias.consultaPorMesAno(dataInicial[0], dataInicial[1]));
        e.setDtFechamento(competencias.consultaPorMesAno(dataFinal[0], dataInicial[1]));
        
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
        BasMovimentacao e = new BasMovimentacao();
        e.setId(dto.id());
        e.setBancoConta(vinculos.findById(dto.bancoContaId()).get());
        //e.setTipo(dto.tipo());
        e.setDsObservacao(dto.descricao());
        
        BasTipoMovimentacao tipoMovimentacao = categorias.consultarPorNome(dto.categoria());        
        e.setTipoMovimentacao(tipoMovimentacao);
        e.setVlCredito(dto.valor().doubleValue()  > 0.0 ? dto.valor() : null);
        e.setVlDebito(dto.valor().doubleValue()  < 0.0 ? dto.valor() : null);
        e.setDtMovimentacao(DateUtils.stringToDate(dto.data()));
        e.setDsObservacao(dto.observacao());
        e.setVlSaldo(dto.saldoApos());

        if(dto.transferenciaId() == null){
            return e;
        }
        Optional<BasMovimentacao> optino = lancamentos.findById(dto.transferenciaId());

        if(optino.isEmpty()){
            e.setVinculado(null);
        }else{
            e.setVinculado(optino.get());
        }
       
        return e;
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
        return categorias.findById(id).orElseThrow(() -> naoEncontrado("Tipo de Movimentacao"));
    }

    private BasBancoConta buscarVinculoEntity(Long id) {
        return vinculos.findById(id).orElseThrow(() -> naoEncontrado("Vínculo"));
    }

    private BasInvestimento buscarInvestimentoEntity(Long id) {
        return investimentos.findById(id).orElseThrow(() -> naoEncontrado("Investimento"));
    }

    private BasMovimentacao buscarLancamentoEntity(Long id) {
        return lancamentos.findById(id).orElseThrow(() -> naoEncontrado("Lançamento"));
    }

    private ResponseStatusException naoEncontrado(String nome) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, nome + " não encontrado");
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }


    public List<Object[]> registrarLancamentos(Integer ano) {
    List<Object[]> listar = lancamentos.registrar(ano);        
        return listar;
    }
}
