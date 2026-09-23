package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.BancoDto;
import br.com.controlefinanceiro.model.BasBanco;
import br.com.controlefinanceiro.repository.BancoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BancoService {
    private final BancoRepository repository;

    public BancoService(BancoRepository repository) {
        this.repository = repository;
    }

    public List<BancoDto> listar() {
        List<BancoDto> lista = repository.findAll().stream().map(this::toDto).toList();
        return lista;
    }

    public BancoDto buscar(Long id) {
        return toDto(repository.findById(id).orElseThrow());
    }

    public BancoDto criar(BancoDto dto) {
        return salvar(dto);
    }

    public BancoDto atualizar(Long id, BancoDto dto) {
        return salvar(new BancoDto(id, dto.nome(), dto.logo(), dto.cor(), dto.corSecundaria()));
    }

    private BancoDto salvar(BancoDto dto) {
        BasBanco banco = new BasBanco();
        banco.setId(dto.id());
        banco.setDsBanco(dto.nome());
        banco.setLogo(dto.logo());
        banco.setCor(dto.cor());
        banco.setCorSecundaria(dto.corSecundaria());
        return toDto(repository.save(banco));
    }

    private BancoDto toDto(BasBanco banco) {
        return new BancoDto(banco.getId(), banco.getDsBanco(), banco.getLogo(), banco.getCor(), banco.getCorSecundaria());
    }
}