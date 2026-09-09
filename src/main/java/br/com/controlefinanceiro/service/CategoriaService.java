package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.CategoriaDto;
import br.com.controlefinanceiro.model.Categoria;
import br.com.controlefinanceiro.repository.CategoriaRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<CategoriaDto> listar() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public CategoriaDto buscar(String id) {
        return toDto(repository.findById(id).orElseThrow());
    }

    public CategoriaDto criar(CategoriaDto dto) {
        return salvar(dto);
    }

    public CategoriaDto atualizar(String id, CategoriaDto dto) {
        return salvar(new CategoriaDto(id, dto.nome(), dto.tipo(), dto.ativo()));
    }

    public CategoriaDto atualizarStatus(String id, String ativo) {
        Categoria categoria = repository.findById(id).orElseThrow();
        categoria.setAtivo(ativo);
        return toDto(repository.save(categoria));
    }

    public void excluir(String id) {
        repository.deleteById(id);
    }

    private CategoriaDto salvar(CategoriaDto dto) {
        Categoria categoria = new Categoria();
        categoria.setId(dto.id() == null ? UUID.randomUUID().toString() : dto.id());
        categoria.setNome(dto.nome());
        categoria.setTipo(dto.tipo());
        categoria.setAtivo(dto.ativo() == null ? "A" : dto.ativo());
        return toDto(repository.save(categoria));
    }

    private CategoriaDto toDto(Categoria categoria) {
        return new CategoriaDto(categoria.getId(), categoria.getNome(), categoria.getTipo(), categoria.getAtivo());
    }
}