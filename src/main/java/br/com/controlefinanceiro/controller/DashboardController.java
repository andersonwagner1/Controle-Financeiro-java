package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.dto.DashboardDto;
import br.com.controlefinanceiro.dto.DashboardCartDto;
import br.com.controlefinanceiro.dto.ContaResumoDto;
import br.com.controlefinanceiro.dto.LancamentoResponseDto;
import br.com.controlefinanceiro.dto.RelatorioMensalDto;
import br.com.controlefinanceiro.dto.SaldoResponseDto;
import br.com.controlefinanceiro.model.emurador.EnumRelatorio;
import br.com.controlefinanceiro.model.emurador.EnumTipoMovimentacao;
import br.com.controlefinanceiro.service.BancoService;
import br.com.controlefinanceiro.service.LancamentoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.EnumMap;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final LancamentoService lancamentoService;
    private final BancoService bancoService;

    public DashboardController(LancamentoService lancamentoService, BancoService bancoService) {
        this.lancamentoService = lancamentoService;
        this.bancoService = bancoService;
    }

    @GetMapping
    public DashboardDto buscarDados(@RequestParam(required = false) Date dataInicial,
            @RequestParam(required = false) Date dataFinal) {
        return new DashboardDto(
                bancoService.listar(),
                lancamentoService.listarContas(),
                lancamentoService.listar(null, dataInicial, dataFinal));
    }

    @GetMapping("/cart")
    public DashboardCartDto buscarResumo(
            @RequestParam(required = false) String competenciaInicial,
            @RequestParam(required = false) String competenciaFinal) {
        YearMonth competenciaFim = competenciaFinal == null || competenciaFinal.isBlank()
                ? YearMonth.now()
                : YearMonth.parse(competenciaFinal);
        YearMonth competenciaInicio = competenciaInicial == null || competenciaInicial.isBlank()
                ? competenciaFim
                : YearMonth.parse(competenciaInicial);
        if (competenciaInicio.isAfter(competenciaFim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A competência inicial não pode ser posterior à competência final");
        }

        List<ContaResumoDto> contas = lancamentoService.listarContas();
        Integer[] competencia = { 1, competenciaFim.getMonthValue(), competenciaFim.getYear() };
        BigDecimal saldoBancario = BigDecimal.ZERO;
        BigDecimal investimentos = BigDecimal.ZERO;
        for (ContaResumoDto conta : contas) {
            SaldoResponseDto saldo = lancamentoService.consultarSaldo(conta.id(), competencia);
            BigDecimal saldoFinal = saldo == null || saldo.saldoFinal() == null
                    ? (competenciaFim.equals(YearMonth.now()) && conta.saldo() != null
                        ? conta.saldo()
                        : BigDecimal.ZERO)
                    : saldo.saldoFinal();
            if ("CC".equals(conta.tipo()) || "CP".equals(conta.tipo())) {
                saldoBancario = saldoBancario.add(saldoFinal);
            } else {
                investimentos = investimentos.add(saldoFinal);
            }
        }

        ZoneId zone = ZoneId.systemDefault();
        LocalDate dataInicio = competenciaInicio.atDay(1);
        LocalDate dataFim = competenciaFim.atEndOfMonth();
        Date dataInicial = Date.from(dataInicio.atStartOfDay(zone).toInstant());
        Date dataFinal = Date.from(dataFim.plusDays(1).atStartOfDay(zone).minusNanos(1).toInstant());
        List<LancamentoResponseDto> lancamentos = lancamentoService.listar(null, dataInicial, dataFinal);

        BigDecimal creditosMes = somarPorTipo(lancamentos, EnumTipoMovimentacao.CREDITO);
        BigDecimal debitosMes = somarPorTipo(lancamentos, EnumTipoMovimentacao.DEBITO);

        return new DashboardCartDto(
                saldoBancario.add(investimentos),
                saldoBancario,
                investimentos,
                creditosMes,
                debitosMes,
                creditosMes.subtract(debitosMes));
    }

    @GetMapping("/relatorio")
    public List<RelatorioMensalDto> buscarRelatorio(@RequestParam(required = false) Integer ano) {
        int anoConsulta = ano == null ? YearMonth.now().getYear() : ano;
        YearMonth dezembroAnterior = YearMonth.of(anoConsulta - 1, 12);
        YearMonth janeiro = YearMonth.of(anoConsulta, 1);
        YearMonth dezembro = YearMonth.of(anoConsulta, 12);
        ZoneId zone = ZoneId.systemDefault();
        Date dataInicial = Date.from(dezembroAnterior.atDay(1).atStartOfDay(zone).toInstant());
        Date dataFinal = Date.from(dezembro.atEndOfMonth().plusDays(1).atStartOfDay(zone).minusNanos(1).toInstant());

        Map<YearMonth, EnumMap<EnumRelatorio, BigDecimal>> totaisPorMes = new java.util.TreeMap<>();
        for (YearMonth competencia = dezembroAnterior; !competencia.isAfter(dezembro); competencia = competencia.plusMonths(1)) {
            EnumMap<EnumRelatorio, BigDecimal> totais = new EnumMap<>(EnumRelatorio.class);
            for (EnumRelatorio codigo : EnumRelatorio.values()) {
                totais.put(codigo, BigDecimal.ZERO);
            }
            totaisPorMes.put(competencia, totais);
        }

        List<LancamentoResponseDto> lancamentos = lancamentoService.listar(null, dataInicial, dataFinal);
        lancamentos.forEach(lancamento -> {
            if (lancamento.icRelatorio() == null || lancamento.valor() == null || lancamento.data() == null) {
                return;
            }
            YearMonth competencia = YearMonth.parse(lancamento.data().substring(0, 7));
            EnumMap<EnumRelatorio, BigDecimal> totais = totaisPorMes.get(competencia);
            if (totais != null) {
                totais.merge(lancamento.icRelatorio(), lancamento.valor().abs(), BigDecimal::add);
            }
        });

        BigDecimal saldoAnterior = totalCredito(totaisPorMes.get(dezembroAnterior))
                .subtract(totalDebito(totaisPorMes.get(dezembroAnterior)));
        java.util.ArrayList<RelatorioMensalDto> relatorio = new java.util.ArrayList<>();
        for (YearMonth competencia = janeiro; !competencia.isAfter(dezembro); competencia = competencia.plusMonths(1)) {
            EnumMap<EnumRelatorio, BigDecimal> totais = totaisPorMes.get(competencia);
            BigDecimal totalCredito = totalCredito(totais);
            BigDecimal totalDebito = totalDebito(totais);
            BigDecimal saldoFinal = totalCredito.subtract(totalDebito);
            relatorio.add(new RelatorioMensalDto(
                    competencia.toString(),
                    competencia.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR")),
                    saldoAnterior,
                    totais.get(EnumRelatorio.RESGATE),
                    totais.get(EnumRelatorio.RENDA),
                    totais.get(EnumRelatorio.CREDITO),
                    totais.get(EnumRelatorio.RENDIMENTO),
                    totalCredito,
                    totais.get(EnumRelatorio.RENDIMENTO_NEGATIVO),
                    totais.get(EnumRelatorio.CARTAO),
                    totais.get(EnumRelatorio.APLICACAO),
                    totais.get(EnumRelatorio.DEBITO),
                    totais.get(EnumRelatorio.MENSAL),
                    totalDebito,
                    saldoFinal));
            saldoAnterior = saldoFinal;
        }
        return relatorio;
    }

    private BigDecimal totalCredito(Map<EnumRelatorio, BigDecimal> totais) {
        return totais.get(EnumRelatorio.RESGATE)
                .add(totais.get(EnumRelatorio.RENDA))
                .add(totais.get(EnumRelatorio.CREDITO))
                .add(totais.get(EnumRelatorio.RENDIMENTO));
    }

    private BigDecimal totalDebito(Map<EnumRelatorio, BigDecimal> totais) {
        return totais.get(EnumRelatorio.RENDIMENTO_NEGATIVO)
                .add(totais.get(EnumRelatorio.CARTAO))
                .add(totais.get(EnumRelatorio.APLICACAO))
                .add(totais.get(EnumRelatorio.DEBITO))
                .add(totais.get(EnumRelatorio.MENSAL));
    }

    private BigDecimal somarPorTipo(List<LancamentoResponseDto> lancamentos, EnumTipoMovimentacao tipo) {
        return lancamentos.stream()
                .filter(lancamento -> tipo == lancamento.tipo())
                .map(LancamentoResponseDto::valor)
                .filter(valor -> valor != null)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
