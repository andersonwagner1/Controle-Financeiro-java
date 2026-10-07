package br.com.controlefinanceiro.service;


import br.com.controlefinanceiro.dto.LancamentoCartaoRequestDto;
import br.com.controlefinanceiro.dto.LancamentoCartaoResponseDto;
import br.com.controlefinanceiro.model.BasLancamentoCredito;
import br.com.controlefinanceiro.model.BasTipoMovimentacao;
import br.com.controlefinanceiro.model.CartaoCredito;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.repository.CartaoCreditoRepository;
import br.com.controlefinanceiro.repository.LancamentoCartaoRepository;
import br.com.controlefinanceiro.repository.TipoMovimentacaoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LancamentoCartaoService {
    private final LancamentoCartaoRepository lancamentos;
    private final CartaoCreditoRepository cartoes;
    private final TipoMovimentacaoRepository tipoMovimentacaoRepository;

    public LancamentoCartaoService(LancamentoCartaoRepository lancamentos,
        TipoMovimentacaoRepository tipoMovimentacaoRepository,
            CartaoCreditoRepository cartoes) {
        this.lancamentos = lancamentos;
        this.cartoes = cartoes;
        this.tipoMovimentacaoRepository = tipoMovimentacaoRepository;
    }
   
   
    @Transactional(readOnly = true)
    public List<LancamentoCartaoResponseDto> listar(LocalDate dataInicio, LocalDate dataFim, Long contaId) {
        
        List<BasLancamentoCredito> resultado = null;
        if(contaId == null || contaId == 0) {
            resultado = lancamentos.localizarLancamentoPorCartao(DateUtils.toDate(dataInicio), DateUtils.toDate(dataFim));
        }else{
            resultado = lancamentos.localizarLancamentoPorCartao(contaId, DateUtils.toDate(dataInicio), DateUtils.toDate(dataFim));            
        }

        return resultado.stream().map(this::toDto).toList();
    }



@Transactional(readOnly = true)
    public LancamentoCartaoResponseDto buscar(Long id) {
        return toDto(buscarEntidade(id));
    }

    @Transactional
    public LancamentoCartaoResponseDto criar(LancamentoCartaoRequestDto dto) {
        validar(dto, null);
        return toDto(lancamentos.save(toEntity(dto)));
    }

    @Transactional
    public LancamentoCartaoResponseDto atualizar(Long id, LancamentoCartaoRequestDto dto) {
        buscarEntidade(id);
        validar(dto, id);
        BasLancamentoCredito lancamento = toEntity(dto);
        lancamento.setId(id);
        return toDto(lancamentos.save(lancamento));
    }

    @Transactional
    public void excluir(Long id) {
        lancamentos.delete(buscarEntidade(id));
    }

    private void validar(LancamentoCartaoRequestDto dto, Long lancamentoIgnoradoId) {
        if (dto.cartaoCreditoId() == null || dto.cartaoCreditoId() == 0 ) throw erro("O cartão de crédito é obrigatório");      
        
        CartaoCredito cartao = cartoes.findById(dto.cartaoCreditoId()).get();
       


        BigDecimal utilizado = lancamentos.somarComprasPorCartao(cartao.getId());
        if (lancamentoIgnoradoId != null) {
            BasLancamentoCredito anterior = buscarEntidade(lancamentoIgnoradoId);
            if (cartao.getId().equals(anterior.getCartaoCredito().getId()))
                utilizado = utilizado.subtract(anterior.getVlCompra());
        }
        if (utilizado.add(dto.valor()).compareTo(cartao.getLimite()) > 0)
            throw erro("Limite disponível do cartão de crédito insuficiente");
    }

    private BasLancamentoCredito buscarEntidade(Long id) {
        return lancamentos.findById(id).get();                
    }

    private BasLancamentoCredito toEntity(LancamentoCartaoRequestDto dto) {
        CartaoCredito cartao = cartoes.findById(dto.cartaoCreditoId()).get();
        BasTipoMovimentacao tipoMovimentacao = tipoMovimentacaoRepository.findById(dto.tipoMovimentacaoId()).get();
        

        BasLancamentoCredito lancamento = new BasLancamentoCredito();
        lancamento.setId(dto.id());
        lancamento.setCartaoCredito(cartao);
        lancamento.setDsObservacao(dto.descricao());        
        lancamento.setTipoMovimentacao(tipoMovimentacao);
        lancamento.setVlCompra(dto.valor());
        lancamento.setDtMovimentacao(dto.data());
        lancamento.setNrParcelas(dto.parcelas());
       
        
        return lancamento;
    }

    private LancamentoCartaoResponseDto toDto(BasLancamentoCredito lancamento) {
        BasTipoMovimentacao tipoMovimentacao = tipoMovimentacaoRepository.findById(lancamento.getTipoMovimentacao().getId()).get();
        LancamentoCartaoResponseDto dto = LancamentoCartaoResponseDto.builder()
                .id(lancamento.getId())
                .cartaoCreditoId(lancamento.getCartaoCredito().getId())
                .tipoMovimentacaoId(tipoMovimentacao.getId())
                .descricao(lancamento.getDsObservacao())                
                .tipoMovimentacao(lancamento.getTipoMovimentacao().getDsTipoMovimentacao())
                .valor(lancamento.getVlCompra())
                .data(DateUtils.toLocalDate(lancamento.getDtMovimentacao()))
                .cartaoCredito(lancamento.getCartaoCredito().getNome())
                .tipo(lancamento.getTipoMovimentacao().getIcTipoMovimentacao())
                .build();
        return dto;
    }


    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }

}
