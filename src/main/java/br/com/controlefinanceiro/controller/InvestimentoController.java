package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.InvestimentoDto;
import br.com.controlefinanceiro.dto.ContaVinculadaDto;
import br.com.controlefinanceiro.service.InvestimentoService;
import br.com.controlefinanceiro.service.BancoContaService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investimentos")
public class InvestimentoController {
    private final InvestimentoService service;
    private final BancoContaService vinculoService;

    public InvestimentoController(InvestimentoService service, BancoContaService vinculoService) {
        this.service = service;
        this.vinculoService = vinculoService;
    }

    @GetMapping
    public List<InvestimentoDto> listar() {
        return service.listar();
    }

    @GetMapping("/contas-vinculadas")
    public List<ContaVinculadaDto> listarContasVinculadas() {
        List<ContaVinculadaDto> lista = vinculoService.listarContasVinculadas();
        return lista;
    }

       @GetMapping("/listar-por-banco/{banco}")
    public List<InvestimentoDto> listarContasVinculadas(@PathVariable Long banco) {
        List<InvestimentoDto> lista = service.listarContasVinculadas(banco);
        return lista;
    }

   

    @GetMapping("/{id}")
    public InvestimentoDto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public InvestimentoDto criar(@RequestBody InvestimentoDto dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public InvestimentoDto atualizar(@PathVariable Long id, @RequestBody InvestimentoDto dto) {
        return service.atualizar(id, dto);
    }
}
