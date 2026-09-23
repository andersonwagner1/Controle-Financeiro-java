package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.TipoMovimentacaoDto;
import br.com.controlefinanceiro.model.BasTipoMovimentacao;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;

import br.com.controlefinanceiro.repository.TipoMovimentacaoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {
    private final TipoMovimentacaoRepository repository;

    public CategoriaService(TipoMovimentacaoRepository repository) {
        this.repository = repository;
    }

    public List<TipoMovimentacaoDto> listar() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public TipoMovimentacaoDto buscar(Long id) {
        return toDto(repository.findById(id).orElseThrow());
    }

    public TipoMovimentacaoDto criar(TipoMovimentacaoDto dto) {
        return salvar(dto);
    }

    public TipoMovimentacaoDto atualizar(Long id, TipoMovimentacaoDto dto) {
        return salvar(new TipoMovimentacaoDto(id, dto.nome(), dto.tipo(), dto.ativo()));
    }

    public TipoMovimentacaoDto atualizarStatus(Long id, String ativo) {
        BasTipoMovimentacao categoria = repository.findById(id).orElseThrow();
        categoria.setIcSituacao(EnumSimNao.valueOf(ativo));
        return toDto(repository.save(categoria));
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    private TipoMovimentacaoDto salvar(TipoMovimentacaoDto dto) {
        BasTipoMovimentacao categoria = new BasTipoMovimentacao();
        categoria.setId(dto.id());
        categoria.setDsTipoMovimentacao(dto.nome());
        categoria.setIcTipoMovimentacao(dto.tipo());
        categoria.setIcSituacao(dto.ativo());
        return toDto(repository.save(categoria));
    }

    private TipoMovimentacaoDto toDto(BasTipoMovimentacao categoria) {
        return new TipoMovimentacaoDto(categoria.getId(), categoria.getDsTipoMovimentacao(), categoria.getIcTipoMovimentacao(), categoria.getIcSituacao());
    }
}