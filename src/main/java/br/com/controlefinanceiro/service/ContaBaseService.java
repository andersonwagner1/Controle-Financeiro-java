package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.ContaBaseDto;
import br.com.controlefinanceiro.model.ContaBase;
import br.com.controlefinanceiro.repository.ContaBaseRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ContaBaseService {
    private final ContaBaseRepository repository;

    public ContaBaseService(ContaBaseRepository repository) {
        this.repository = repository;
    }

    public List<ContaBaseDto> listar() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public ContaBaseDto buscar(String id) {
        return toDto(repository.findById(id).orElseThrow());
    }

    public ContaBaseDto criar(ContaBaseDto dto) {
        return salvar(dto);
    }

    public ContaBaseDto atualizar(String id, ContaBaseDto dto) {
        return salvar(new ContaBaseDto(id, dto.descricao(), dto.tipo()));
    }

    private ContaBaseDto salvar(ContaBaseDto dto) {
        ContaBase conta = new ContaBase();
        conta.setId(dto.id() == null ? UUID.randomUUID().toString() : dto.id());
        conta.setDescricao(dto.descricao());
        conta.setTipo(dto.tipo());
        return toDto(repository.save(conta));
    }

    private ContaBaseDto toDto(ContaBase conta) {
        return new ContaBaseDto(conta.getId(), conta.getDescricao(), conta.getTipo());
    }
}