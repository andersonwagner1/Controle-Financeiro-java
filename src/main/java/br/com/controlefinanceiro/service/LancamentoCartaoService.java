package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.LancamentoCartaoDto;
import br.com.controlefinanceiro.model.CartaoCredito;
import br.com.controlefinanceiro.model.LancamentoCartao;
import br.com.controlefinanceiro.repository.CartaoCreditoRepository;
import br.com.controlefinanceiro.repository.LancamentoCartaoRepository;
import br.com.controlefinanceiro.repository.VinculoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LancamentoCartaoService {
    private final LancamentoCartaoRepository lancamentos;
    private final CartaoCreditoRepository cartoes;
    private final VinculoRepository vinculos;

    public LancamentoCartaoService(LancamentoCartaoRepository lancamentos,
            CartaoCreditoRepository cartoes, VinculoRepository vinculos) {
        this.lancamentos = lancamentos;
        this.cartoes = cartoes;
        this.vinculos = vinculos;
    }

    public List<LancamentoCartaoDto> listar(String contaId) {
        List<LancamentoCartao> resultado = contaId == null ? lancamentos.findAll()
                : lancamentos.findByContaIdOrderByDataDesc(contaId);
        return resultado.stream().map(this::toDto).toList();
    }

    public LancamentoCartaoDto buscar(String id) {
        return toDto(buscarEntidade(id));
    }

    @Transactional
    public LancamentoCartaoDto criar(LancamentoCartaoDto dto) {
        validar(dto, null);
        return toDto(lancamentos.save(toEntity(dto)));
    }

    @Transactional
    public LancamentoCartaoDto atualizar(String id, LancamentoCartaoDto dto) {
        buscarEntidade(id);
        validar(dto, id);
        LancamentoCartao lancamento = toEntity(dto);
        lancamento.setId(id);
        return toDto(lancamentos.save(lancamento));
    }

    @Transactional
    public void excluir(String id) {
        lancamentos.delete(buscarEntidade(id));
    }

    private void validar(LancamentoCartaoDto dto, String lancamentoIgnoradoId) {
        if (dto.vinculoId() == null || dto.vinculoId().isBlank())
            throw erro("O vínculo é obrigatório");      
        CartaoCredito cartao = cartoes.findById(dto.vinculoId())
                .orElseThrow(() -> naoEncontrado("Cartão de crédito"));
       // if (!vinculos.existsById(dto.vinculoId()))
        //    throw naoEncontrado("Vínculo");
        //if (!cartao.getVinculoId().equals(dto.vinculoId()))
        //    throw erro("O cartão de crédito não pertence ao vínculo informado");
        if (!"debito".equalsIgnoreCase(dto.tipo()))
            throw erro("Lançamentos em cartão de crédito devem ser do tipo débito");
        //if (dto.valor() == null || dto.valor().signum() <= 0)
        //    throw erro("O valor da compra no cartão deve ser positivo");

        BigDecimal utilizado = lancamentos.somarComprasPorCartao(cartao.getId());
        if (lancamentoIgnoradoId != null) {
            LancamentoCartao anterior = buscarEntidade(lancamentoIgnoradoId);
            if (cartao.getId().equals(anterior.getCartaoCreditoId()))
                utilizado = utilizado.subtract(anterior.getValor());
        }
        if (utilizado.add(dto.valor()).compareTo(cartao.getLimite()) > 0)
            throw erro("Limite disponível do cartão de crédito insuficiente");
    }

    private LancamentoCartao buscarEntidade(String id) {
        return lancamentos.findById(id)
                .orElseThrow(() -> naoEncontrado("Lançamento de cartão"));
    }

    private LancamentoCartao toEntity(LancamentoCartaoDto dto) {
        LancamentoCartao lancamento = new LancamentoCartao();
        lancamento.setId(dto.id() == null ? UUID.randomUUID().toString() : dto.id());
        lancamento.setContaId(dto.contaId());
        lancamento.setCartaoCreditoId(dto.vinculoId());
        lancamento.setTipo(dto.tipo());
        lancamento.setDescricao(dto.descricao());
        lancamento.setCategoria(dto.categoria());
        lancamento.setValor(dto.valor());
        lancamento.setData(dto.data());
        lancamento.setObservacao(dto.observacao());
        lancamento.setSaldoApos(dto.saldoApos());
        lancamento.setTransferenciaId(dto.transferenciaId());
        return lancamento;
    }

    private LancamentoCartaoDto toDto(LancamentoCartao lancamento) {
        return new LancamentoCartaoDto(lancamento.getId(), lancamento.getContaId(),
                lancamento.getCartaoCreditoId(), lancamento.getTipo(), lancamento.getDescricao(),
                lancamento.getCategoria(), lancamento.getValor(), lancamento.getData(),
                lancamento.getObservacao(), lancamento.getSaldoApos(), lancamento.getTransferenciaId());
    }

    private ResponseStatusException naoEncontrado(String nome) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, nome + " não encontrado");
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}