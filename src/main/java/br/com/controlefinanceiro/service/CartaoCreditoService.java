package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.CartaoCreditoDto;
import br.com.controlefinanceiro.model.CartaoCredito;
import br.com.controlefinanceiro.repository.CartaoCreditoRepository;
import br.com.controlefinanceiro.repository.LancamentoCartaoRepository;
import jakarta.persistence.Transient;
import br.com.controlefinanceiro.repository.BancoRepository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartaoCreditoService {
    private final CartaoCreditoRepository cartoes;
    private final BancoRepository bancoRepository;
        private final LancamentoCartaoRepository lancamentos;

    public CartaoCreditoService(CartaoCreditoRepository cartoes, 
        BancoRepository bancoRepository,
            LancamentoCartaoRepository lancamentos, 
        CartaoCreditoRepository cartaoCreditoRepository) {
        this.cartoes = cartoes;
        this.bancoRepository = bancoRepository;
        this.lancamentos = lancamentos;
    
    }
@Transient
    public List<CartaoCreditoDto> listar() {
        return cartoes.findAll().stream().map(this::toDto).toList();
    }
@Transient
    public CartaoCreditoDto buscar(Long id) {
        return toDto(buscarEntidade(id));
    }
@Transient
    public CartaoCreditoDto criar(CartaoCreditoDto dto) {
        return salvar(dto, null);
    }
@Transient
    public CartaoCreditoDto atualizar(Long id, CartaoCreditoDto dto) {
        buscarEntidade(id);
        return salvar(dto, id);
    }

    private CartaoCreditoDto salvar(CartaoCreditoDto dto, Long id) {
        if (!bancoRepository.existsById(dto.vinculoId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo não encontrado");
        }

        //cartaoCreditoRepository.findById(id);

        CartaoCredito cartao = new CartaoCredito();
        cartao.setId(id);
        cartao.setNome(dto.nome());
        cartao.setBanco(bancoRepository.findById(dto.vinculoId()).orElse(null));
        cartao.setLimite(dto.limite());
        cartao.setDiaFechamento(dto.diaFechamento());
        cartao.setDiaVencimento(dto.diaVencimento());
        cartao.setDataAbertura(dto.dataAbertura());
        cartao.setDataFechamento(dto.dataFechamento());
        return toDto(cartoes.save(cartao));
    }


    @Transient
    private CartaoCredito buscarEntidade(Long id) {
        return cartoes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartão de crédito não encontrado"));
    }

    private CartaoCreditoDto toDto(CartaoCredito cartao) {
        BigDecimal utilizado = lancamentos.somarComprasPorCartao(cartao.getId());
        BigDecimal limiteDisponivel = cartao.getLimite().subtract(utilizado == null ? BigDecimal.ZERO : utilizado);
        return new CartaoCreditoDto(cartao.getId(), 
                cartao.getNome(), 
                cartao.getBanco ().getId(), 
                cartao.getLimite(),
                cartao.getDiaFechamento(), 
                cartao.getDiaVencimento(), 
                limiteDisponivel, 
                cartao.getDataFechamento(), 
                cartao.getDataAbertura());
    }
}
