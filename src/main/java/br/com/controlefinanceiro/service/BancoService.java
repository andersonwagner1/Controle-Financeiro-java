package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.BancoDto;
import br.com.controlefinanceiro.model.Banco;
import br.com.controlefinanceiro.repository.BancoRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BancoService {
    private final BancoRepository repository;

    public BancoService(BancoRepository repository) { this.repository = repository; }
    public List<BancoDto> listar() { 
        List<BancoDto> lista =  repository.findAll().stream().map(this::toDto).toList(); 
        return lista;
    }
    public BancoDto buscar(String id) { return toDto(repository.findById(id).orElseThrow()); }
    public BancoDto criar(BancoDto dto) { return salvar(dto); }
    public BancoDto atualizar(String id, BancoDto dto) { return salvar(new BancoDto(id, dto.nome(), dto.logo(), dto.cor(), dto.corSecundaria())); }
    private BancoDto salvar(BancoDto dto) { Banco banco = new Banco(); banco.setId(dto.id() == null ? UUID.randomUUID().toString() : dto.id()); banco.setNome(dto.nome()); banco.setLogo(dto.logo()); banco.setCor(dto.cor()); banco.setCorSecundaria(dto.corSecundaria()); return toDto(repository.save(banco)); }
    private BancoDto toDto(Banco banco) { return new BancoDto(banco.getId(), banco.getNome(), banco.getLogo(), banco.getCor(), banco.getCorSecundaria()); }
}