package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.dto.ContaVinculadaDto;
import br.com.controlefinanceiro.dto.VinculoDto;

import br.com.controlefinanceiro.model.BasBanco;
import br.com.controlefinanceiro.model.BasBancoConta;
import br.com.controlefinanceiro.model.BasCompetencia;
import br.com.controlefinanceiro.model.BasConta;
import br.com.controlefinanceiro.model.emurador.EnumSimNao;
import br.com.controlefinanceiro.model.util.DateUtils;
import br.com.controlefinanceiro.repository.BancoRepository;
import br.com.controlefinanceiro.repository.BasCompetenciaRespository;
import br.com.controlefinanceiro.repository.ContaBaseRepository;
import br.com.controlefinanceiro.repository.BancoContaRepository;
import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


@Service
public class BancoContaService {
    private final BancoContaRepository repository;
    private final BancoRepository bancoRepository;
    private final ContaBaseRepository contaBaseRepository;
    private final BasCompetenciaRespository competenciaRespository;

    public BancoContaService(BancoContaRepository repository, BancoRepository bancoRepository,
            ContaBaseRepository contaBaseRepository, BasCompetenciaRespository competenciaRespository) {
        this.repository = repository;
        this.bancoRepository = bancoRepository;
        this.contaBaseRepository = contaBaseRepository;
        this.competenciaRespository = competenciaRespository;
    }

    public List<VinculoDto> listar() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public List<VinculoDto> listarAtivos() {
        return repository.findAll().stream()
              //  .filter(Vinculo::isAtiva)
                .map(this::toDto)
                .toList();
    }

    public List<ContaVinculadaDto> listarContasVinculadas() {
        return repository.findAll().stream()
             //   .filter(Vinculo::isAtiva)
                .map(this::toContaVinculadaDto)
                .toList();
    }

    public VinculoDto buscar(Long id) {
        return toDto(repository.findById(id).orElseThrow());
    }

    public VinculoDto criar(VinculoDto dto) {
        return salvar(dto);
    }

    public VinculoDto atualizar(Long id, VinculoDto dto) {
        return salvar(new VinculoDto(id, dto.bancoId(), dto.contaBaseId(), dto.saldo(), dto.dataInicio(), dto.dataFim(),
                dto.rentabilidade(), dto.vencimento(), dto.ativa()));
    }

    public VinculoDto atualizarSaldo(Long id, BigDecimal saldo) {
        BasBancoConta vinculo = repository.findById(id).orElseThrow();
        vinculo.setVlSaldoAtual(saldo);
        return toDto(repository.save(vinculo));
    }

    private VinculoDto salvar(VinculoDto dto) {
        Integer[] dataInicial = DateUtils.getMesEAno(dto.dataInicio());
        Integer[] dataFinal = DateUtils.getMesEAno(dto.dataInicio());


        BasBancoConta vinculo = new BasBancoConta();
        vinculo.setId(dto.id());
        vinculo.setBanco(bancoRepository.findById(dto.bancoId()).get());
        vinculo.setConta(contaBaseRepository.findById(dto.contaBaseId()).get());
        vinculo.setVlSaldoAtual(dto.saldo() == null ? BigDecimal.ZERO : dto.saldo());
        vinculo.setDtAbertura(competenciaRespository.consultaPorMesAno( dataInicial[0], dataInicial[1]));
        vinculo.setDtFechamento(competenciaRespository.consultaPorMesAno( dataFinal[0], dataFinal[1]));
       // vinculo.setRentabilidade(dto.rentabilidade());
       // vinculo.setVencimento(dto.vencimento());
        vinculo.setIcSituacao(EnumSimNao.SIM);
        return toDto(repository.save(vinculo));
    }

    private VinculoDto toDto(BasBancoConta vinculo) {
      return new VinculoDto(
            vinculo.getId(), 
            vinculo.getBanco().getId(), 
            vinculo.getConta().getId(),
            vinculo.getVlSaldoAtual(),
            DateUtils.getPrimeiroDiaDoMes(vinculo.getDtAbertura().getDsMesAno()), 
            Optional.ofNullable(vinculo.getDtFechamento())
                .map(BasCompetencia::getDsMesAno)
                .map(DateUtils::getUltimoDiaDoMes)
                .orElse(null),
            null, 
            null,
            true
        );
    }

    private ContaVinculadaDto toContaVinculadaDto(BasBancoConta bancoConta) {
        BasBanco banco = bancoRepository.findById(bancoConta.getBanco().getId()).orElse(null);
        BasConta conta = contaBaseRepository.findById(bancoConta.getConta().getId()).orElse(null);
        
        return new ContaVinculadaDto(bancoConta.getId(), bancoConta.getBanco().getId(),
                banco == null ? null : banco.getDsBanco(), bancoConta.getConta().getId(),
                conta == null ? null : conta.getDsConta(), bancoConta.getVlSaldoAtual(), true);
    }
}