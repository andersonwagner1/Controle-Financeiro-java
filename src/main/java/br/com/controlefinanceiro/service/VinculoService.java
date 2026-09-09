package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.VinculoDto;
import br.com.controlefinanceiro.model.Vinculo;
import br.com.controlefinanceiro.repository.VinculoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class VinculoService {
    private final VinculoRepository repository;

    public VinculoService(VinculoRepository repository) {
        this.repository = repository;
    }

    public List<VinculoDto> listar() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public VinculoDto buscar(String id) {
        return toDto(repository.findById(id).orElseThrow());
    }

    public VinculoDto criar(VinculoDto dto) {
        return salvar(dto);
    }

    public VinculoDto atualizar(String id, VinculoDto dto) {
        return salvar(new VinculoDto(id, dto.bancoId(), dto.contaBaseId(), dto.saldo(), dto.dataInicio(), dto.dataFim(),
                dto.rentabilidade(), dto.vencimento(), dto.ativa()));
    }

    public VinculoDto atualizarSaldo(String id, BigDecimal saldo) {
        Vinculo vinculo = repository.findById(id).orElseThrow();
        vinculo.setSaldo(saldo);
        return toDto(repository.save(vinculo));
    }

    private VinculoDto salvar(VinculoDto dto) {
        Vinculo vinculo = new Vinculo();
        vinculo.setId(dto.id() == null ? UUID.randomUUID().toString() : dto.id());
        vinculo.setBancoId(dto.bancoId());
        vinculo.setContaBaseId(dto.contaBaseId());
        vinculo.setSaldo(dto.saldo() == null ? BigDecimal.ZERO : dto.saldo());
        vinculo.setDataInicio(dto.dataInicio());
        vinculo.setDataFim(dto.dataFim());
        vinculo.setRentabilidade(dto.rentabilidade());
        vinculo.setVencimento(dto.vencimento());
        vinculo.setAtiva(dto.ativa() == null || dto.ativa());
        return toDto(repository.save(vinculo));
    }

    private VinculoDto toDto(Vinculo vinculo) {
        return new VinculoDto(vinculo.getId(), vinculo.getBancoId(), vinculo.getContaBaseId(), vinculo.getSaldo(),
                vinculo.getDataInicio(), vinculo.getDataFim(), vinculo.getRentabilidade(), vinculo.getVencimento(),
                vinculo.isAtiva());
    }
}