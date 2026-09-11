package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.*;
import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FinanceiroService {
    private final BancoRepository bancos;
    private final ContaBaseRepository contasBase;
    private final CategoriaRepository categorias;
    private final VinculoRepository vinculos;
        private final InvestimentoRepository investimentos;
    private final LancamentoRepository lancamentos;

    public FinanceiroService(BancoRepository bancos, ContaBaseRepository contasBase,
            CategoriaRepository categorias, VinculoRepository vinculos, InvestimentoRepository investimentos,
            LancamentoRepository lancamentos) {
        this.bancos = bancos;
        this.contasBase = contasBase;
        this.categorias = categorias;
        this.vinculos = vinculos;
        this.investimentos = investimentos;
        this.lancamentos = lancamentos;
    }

    public List<BancoDto> listarBancos() {
        return bancos.findAll().stream().map(this::toDto).toList();
    }

    public BancoDto buscarBanco(String id) {
        return toDto(bancos.findById(id).orElseThrow(() -> naoEncontrado("Banco")));
    }

    public BancoDto salvarBanco(BancoDto dto) {
        return toDto(bancos.save(toEntity(dto)));
    }

    public List<ContaBaseDto> listarContasBase() {
        return contasBase.findAll().stream().map(this::toDto).toList();
    }

    public ContaBaseDto buscarContaBase(String id) {
        return toDto(contasBase.findById(id).orElseThrow(() -> naoEncontrado("Conta base")));
    }

    public ContaBaseDto salvarContaBase(ContaBaseDto dto) {
        return toDto(contasBase.save(toEntity(dto)));
    }

    public List<CategoriaDto> listarCategorias() {
        return categorias.findAll().stream().map(this::toDto).toList();
    }

    public CategoriaDto buscarCategoria(String id) {
        return toDto(categorias.findById(id).orElseThrow(() -> naoEncontrado("Categoria")));
    }

    public CategoriaDto salvarCategoria(CategoriaDto dto) {
        return toDto(categorias.save(toEntity(dto)));
    }

    public CategoriaDto atualizarStatusCategoria(String id, String ativo) {
        Categoria categoria = buscarCategoriaEntity(id);
        categoria.setAtivo(ativo);
        return toDto(categorias.save(categoria));
    }

    public void excluirCategoria(String id) {
        categorias.delete(buscarCategoriaEntity(id));
    }

    public List<VinculoDto> listarVinculos() {
        return vinculos.findAll().stream().map(this::toDto).toList();
    }

    public VinculoDto buscarVinculo(String id) {
        return toDto(buscarVinculoEntity(id));
    }

    public VinculoDto salvarVinculo(VinculoDto dto) {
        return toDto(vinculos.save(toEntity(dto)));
    }

    public VinculoDto atualizarSaldo(String id, BigDecimal saldo) {
        Vinculo vinculo = buscarVinculoEntity(id);
        vinculo.setSaldo(saldo);
        return toDto(vinculos.save(vinculo));
    }

    public List<ContaResumoDto> listarContas() {
        return vinculos.findAll().stream().map(vinculo -> {
            ContaBase base = contasBase.findById(vinculo.getContaBaseId()).orElse(null);
            return new ContaResumoDto(vinculo.getId(), vinculo.getBancoId(), base == null ? null : base.getTipo(),
                    base == null ? null : base.getDescricao(), vinculo.getSaldo(), vinculo.isAtiva(),
                    vinculo.getRentabilidade(), vinculo.getVencimento(), vinculo.getDataInicio());
        }).toList();
    }

    public ContaResumoDto buscarConta(String id) {
        return listarContas().stream().filter(conta -> id.equals(conta.id()))
                .findFirst().orElseThrow(() -> naoEncontrado("Conta"));
    }

    public List<LancamentoDto> listarLancamentos(String contaId, LocalDate dataInicial, LocalDate dataFinal) {
        List<Lancamento> resultado = lancamentos.buscarPorFiltros(contaId, dataInicial, dataFinal);
        return resultado.stream().map(this::toDto).toList();
    }

    public LancamentoDto buscarLancamento(String id) {
        return toDto(buscarLancamentoEntity(id));
    }

    @Transactional
    public LancamentoDto criarLancamento(LancamentoDto dto) {
        ajustarSaldo(dto.contaId(), dto.tipo(), dto.valor());
        return toDto(lancamentos.save(toEntity(dto)));
    }

    @Transactional
    public LancamentoDto atualizarLancamento(String id, LancamentoDto dto) {
        Lancamento antigo = buscarLancamentoEntity(id);
        ajustarSaldo(antigo.getContaId(), antigo.getTipo(), antigo.getValor().negate());
        Lancamento novo = toEntity(dto);
        novo.setId(id);
        ajustarSaldo(novo.getContaId(), novo.getTipo(), novo.getValor());
        return toDto(lancamentos.save(novo));
    }

    @Transactional
    public void excluirLancamento(String id) {
        Lancamento lancamento = buscarLancamentoEntity(id);
        ajustarSaldo(lancamento.getContaId(), lancamento.getTipo(), lancamento.getValor().negate());
        lancamentos.delete(lancamento);
    }

    @Transactional
    public TransferenciaDto transferir(TransferenciaDto request) {
        BigDecimal saldoOrigem = buscarSaldoTransferencia(request.contaOrigemId());
        if (request.contaOrigemId().equals(request.contaDestinoId()))
            throw erro("As contas devem ser diferentes");
        if (request.valor() == null || request.valor().signum() <= 0)
            throw erro("O valor deve ser positivo");
        if (saldoOrigem.compareTo(request.valor()) < 0)
            throw erro("Saldo insuficiente");

        String transferenciaId = request.id() == null ? UUID.randomUUID().toString() : request.id();
        lancamentos.save(novoLancamento(request.contaOrigemId(), "debito", "Transferência Enviada", request, transferenciaId));
        lancamentos
            .save(novoLancamento(request.contaDestinoId(), "credito", "Transferência Recebida", request, transferenciaId));
        ajustarSaldo(request.contaOrigemId(), "debito", request.valor());
        ajustarSaldo(request.contaDestinoId(), "credito", request.valor());
        return new TransferenciaDto(transferenciaId, request.contaOrigemId(), request.contaDestinoId(), request.valor(), request.data(),
                request.descricao(), request.investimentoId());
    }

    private Lancamento novoLancamento(String contaId, String tipo, String categoria, TransferenciaDto request,
            String transferenciaId) {
        Lancamento lancamento = new Lancamento();
        lancamento.setContaId(contaId);
        lancamento.setTipo(tipo);
        lancamento.setDescricao(request.descricao());
        lancamento.setCategoria(categoria);
        lancamento.setValor(request.valor());
        lancamento.setData(request.data());
        lancamento.setTransferenciaId(transferenciaId);
        return lancamento;
    }

    private void ajustarSaldo(String contaId, String tipo, BigDecimal valor) {
        BigDecimal delta = "credito".equalsIgnoreCase(tipo) ? valor : valor.negate();
        Vinculo vinculo = vinculos.findById(contaId).orElse(null);
        if (vinculo != null) {
            vinculo.setSaldo(vinculo.getSaldo() == null ? delta : vinculo.getSaldo().add(delta));
            vinculos.save(vinculo);
            return;
        }
        Investimento investimento = buscarInvestimentoEntity(contaId);        
        investimentos.save(investimento);
    }

    private BigDecimal buscarSaldoTransferencia(String contaId) {
        Vinculo vinculo = vinculos.findById(contaId).orElse(null);
        if (vinculo != null)
            return vinculo.getSaldo() == null ? BigDecimal.ZERO : vinculo.getSaldo();
        Investimento investimento = buscarInvestimentoEntity(contaId);
        return null;
    }

    private Banco toEntity(BancoDto dto) {
        Banco e = new Banco();
        e.setId(id(dto.id()));
        e.setNome(dto.nome());
        e.setLogo(dto.logo());
        e.setCor(dto.cor());
        e.setCorSecundaria(dto.corSecundaria());
        return e;
    }

    private BancoDto toDto(Banco e) {
        return new BancoDto(e.getId(), e.getNome(), e.getLogo(), e.getCor(), e.getCorSecundaria());
    }

    private ContaBase toEntity(ContaBaseDto dto) {
        ContaBase e = new ContaBase();
        e.setId(id(dto.id()));
        e.setDescricao(dto.descricao());
        e.setTipo(dto.tipo());
        return e;
    }

    private ContaBaseDto toDto(ContaBase e) {
        return new ContaBaseDto(e.getId(), e.getDescricao(), e.getTipo());
    }

    private Categoria toEntity(CategoriaDto dto) {
        Categoria e = new Categoria();
        e.setId(id(dto.id()));
        e.setNome(dto.nome());
        e.setTipo(dto.tipo());
        e.setAtivo(dto.ativo() == null ? "A" : dto.ativo());
        return e;
    }

    private CategoriaDto toDto(Categoria e) {
        return new CategoriaDto(e.getId(), e.getNome(), e.getTipo(), e.getAtivo());
    }

    private Vinculo toEntity(VinculoDto dto) {
        Vinculo e = new Vinculo();
        e.setId(id(dto.id()));
        e.setBancoId(dto.bancoId());
        e.setContaBaseId(dto.contaBaseId());
        e.setSaldo(dto.saldo() == null ? BigDecimal.ZERO : dto.saldo());
        e.setDataInicio(dto.dataInicio());
        e.setDataFim(dto.dataFim());
        e.setRentabilidade(dto.rentabilidade());
        e.setVencimento(dto.vencimento());
        e.setAtiva(dto.ativa() == null || dto.ativa());
        return e;
    }

    private VinculoDto toDto(Vinculo e) {
        return new VinculoDto(e.getId(), e.getBancoId(), e.getContaBaseId(), e.getSaldo(), e.getDataInicio(),
                e.getDataFim(), e.getRentabilidade(), e.getVencimento(), e.isAtiva());
    }

    private Lancamento toEntity(LancamentoDto dto) {
        Lancamento e = new Lancamento();
        e.setId(id(dto.id()));
        e.setContaId(dto.contaId());
        e.setTipo(dto.tipo());
        e.setDescricao(dto.descricao());
        e.setCategoria(dto.categoria());
        e.setValor(dto.valor());
        e.setData(dto.data());
        e.setObservacao(dto.observacao());
        e.setSaldoApos(dto.saldoApos());
        e.setTransferenciaId(dto.transferenciaId());
        return e;
    }

    private LancamentoDto toDto(Lancamento e) {
        return new LancamentoDto(e.getId(), e.getContaId(), e.getTipo(), e.getDescricao(), e.getCategoria(),
                e.getValor(), e.getData(), e.getObservacao(), e.getSaldoApos(), e.getTransferenciaId(), e.getInvestimentoId());
    }

    private String id(String id) {
        return id == null ? UUID.randomUUID().toString() : id;
    }

    private Categoria buscarCategoriaEntity(String id) {
        return categorias.findById(id).orElseThrow(() -> naoEncontrado("Categoria"));
    }

    private Vinculo buscarVinculoEntity(String id) {
        return vinculos.findById(id).orElseThrow(() -> naoEncontrado("Vínculo"));
    }

    private Investimento buscarInvestimentoEntity(String id) {
        return investimentos.findById(id).orElseThrow(() -> naoEncontrado("Investimento"));
    }

    private Lancamento buscarLancamentoEntity(String id) {
        return lancamentos.findById(id).orElseThrow(() -> naoEncontrado("Lançamento"));
    }

    private ResponseStatusException naoEncontrado(String nome) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, nome + " não encontrado");
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
