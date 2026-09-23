package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.InvestimentoDto;
import br.com.controlefinanceiro.model.BasInvestimento;

import br.com.controlefinanceiro.repository.InvestimentoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InvestimentoService {
    private final InvestimentoRepository repository;

    public InvestimentoService(InvestimentoRepository repository) {
        this.repository = repository;
    }

    public List<InvestimentoDto> listar() {
        return repository.findAll().stream()
               // .filter(Investimento::isAtivo)
                .map(this::toDto)
                .toList();
    }

    public InvestimentoDto buscar(Long id) {
        return toDto(buscarEntidade(id));
    }

    public InvestimentoDto criar(InvestimentoDto dto) {
        return salvar(dto, null);
    }

    public InvestimentoDto atualizar(Long id, InvestimentoDto dto) {
        buscarEntidade(id);
        return salvar(dto, id);
    }


    public BasInvestimento buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investimento não encontrado"));
    }

    private InvestimentoDto salvar(InvestimentoDto dto, Long id) {
        BasInvestimento investimento = new BasInvestimento();
        investimento.setId(dto.id());
       
        investimento.setDsInvestimento(dto.nome());
        investimento.setIcSituacao(dto.ativo());
        return toDto(repository.save(investimento));
    }

    private InvestimentoDto toDto(BasInvestimento investimento) {
        return new InvestimentoDto(investimento.getId(), investimento.getDsInvestimento(), investimento.getIcSituacao());
    }
}