package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.ContaBaseDto;
import br.com.controlefinanceiro.model.BasConta;

import br.com.controlefinanceiro.repository.ContaBaseRepository;
import java.util.List;
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

    public ContaBaseDto buscar(Long id) {
        return toDto(repository.findById(id).orElseThrow());
    }

    public ContaBaseDto criar(ContaBaseDto dto) {
        return salvar(dto);
    }

    public ContaBaseDto atualizar(Long id, ContaBaseDto dto) {
        return salvar(new ContaBaseDto(id, dto.descricao(), dto.tipo()));
    }

    private ContaBaseDto salvar(ContaBaseDto dto) {
        BasConta conta = new BasConta();
        conta.setId(dto.id());
        conta.setDsConta(dto.descricao());
        conta.setIcTipo(dto.tipo());
        return toDto(repository.save(conta));
    }

    private ContaBaseDto toDto(BasConta conta) {
        return new ContaBaseDto(conta.getId(), conta.getDsConta(), conta.getIcTipo());
    }
}