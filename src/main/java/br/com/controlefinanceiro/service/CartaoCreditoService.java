package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.CartaoCreditoDto;
import br.com.controlefinanceiro.model.CartaoCredito;
import br.com.controlefinanceiro.repository.CartaoCreditoRepository;
import br.com.controlefinanceiro.repository.LancamentoCartaoRepository;
import br.com.controlefinanceiro.repository.BancoContaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartaoCreditoService {
    private final CartaoCreditoRepository cartoes;
    private final BancoContaRepository vinculos;
        private final LancamentoCartaoRepository lancamentos;

    public CartaoCreditoService(CartaoCreditoRepository cartoes, 
        BancoContaRepository vinculos,
            LancamentoCartaoRepository lancamentos) {
        this.cartoes = cartoes;
        this.vinculos = vinculos;
        this.lancamentos = lancamentos;
    }

    public List<CartaoCreditoDto> listar() {
        return cartoes.findAll().stream().map(this::toDto).toList();
    }

    public CartaoCreditoDto buscar(String id) {
        return toDto(buscarEntidade(id));
    }

    public CartaoCreditoDto criar(CartaoCreditoDto dto) {
        return salvar(dto, null);
    }

    public CartaoCreditoDto atualizar(String id, CartaoCreditoDto dto) {
        buscarEntidade(id);
        return salvar(dto, id);
    }

    private CartaoCreditoDto salvar(CartaoCreditoDto dto, String id) {
        if (!vinculos.existsById(dto.vinculoId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo não encontrado");
        }
        CartaoCredito cartao = new CartaoCredito();
        cartao.setId(id == null ? (dto.id() == null ? UUID.randomUUID().toString() : dto.id().toString()) : id);
        cartao.setNome(dto.nome());
        cartao.setBancoConta( vinculos.findById(dto.vinculoId()).get());
        cartao.setLimite(dto.limite());
        cartao.setDiaFechamento(dto.diaFechamento());
        cartao.setDiaVencimento(dto.diaVencimento());
        return toDto(cartoes.save(cartao));
    }

    private CartaoCredito buscarEntidade(String id) {
        return cartoes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartão de crédito não encontrado"));
    }

    private CartaoCreditoDto toDto(CartaoCredito cartao) {
        BigDecimal utilizado = lancamentos.somarComprasPorCartao(cartao.getId());
        BigDecimal limiteDisponivel = cartao.getLimite().subtract(utilizado == null ? BigDecimal.ZERO : utilizado);
        return new CartaoCreditoDto(cartao.getId(), 
                cartao.getNome(), 
                cartao.getBancoConta().getId(), 
                cartao.getLimite(),
                cartao.getDiaFechamento(), 
                cartao.getDiaVencimento(), 
                limiteDisponivel, 
                cartao.getDataFechamento(), 
                cartao.getDataAbertura());
    }
}
