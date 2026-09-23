package br.com.controlefinanceiro.model.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/yyyy");


    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_PATTERN_Z = "yyyy-MM-dd";

    /**
     * Converte uma String em java.util.Date usando o formato padrão (dd/MM/yyyy).
     */
    public static Date stringToDate(String dateStr)  {
        try{
        return stringToDate(dateStr, DEFAULT_PATTERN);
        }catch(ParseException e){
            e.printStackTrace();
            return null;
        }
    }




    /**
     * Converte um java.util.Date em String usando o formato padrão (dd/MM/yyyy).
     */
    public static String dateToString(Date date) {
        return dateToString(date, DEFAULT_PATTERN_Z);
    }

    /**
     * Converte um java.util.Date em String com um formato customizado.
     */
    public static String dateToString(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    /**
     * Converte uma String em java.util.Date com um formato customizado.
     */
    public static Date stringToDate(String dateStr, String pattern) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setLenient(false); // Impede datas inválidas como 30/02/2026
        return formatter.parse(dateStr);
    }

    /**
     * Retorna o primeiro dia do mês/ano informado.
     * @param mesAno Mês e ano no formato "MM/yyyy" (ex: "02/2026")
     * @return LocalDate referente ao primeiro dia
     */
    public static LocalDate getPrimeiroDiaDoMes(String mesAno) {
        YearMonth ym = YearMonth.parse(mesAno, FORMATTER);
        return ym.atDay(1);
    }

    /**
     * Retorna o último dia do mês/ano informado (trata anos bissextos automaticamente).
     * @param mesAno Mês e ano no formato "MM/yyyy" (ex: "02/2026")
     * @return LocalDate referente ao último dia
     */
    public static LocalDate getUltimoDiaDoMes(String mesAno) {
        if(mesAno == null){ return null;}
        YearMonth ym = YearMonth.parse(mesAno, FORMATTER);
        return ym.atEndOfMonth();
    }

    /**
     * Recebe um LocalDate e retorna um vetor contendo [mês, ano]
     * @param data Objeto LocalDate
     * @return String[] onde [0] = mês (MM) e [1] = ano (yyyy)
     */
    public static Integer[] getMesEAno(LocalDate data) {
        if (data == null) return new Integer[0];
        
        Integer mes = Integer.parseInt(String.format("%02d", data.getMonthValue()));
        Integer ano = Integer.parseInt(String.valueOf(data.getYear()));
        
        return new Integer[]{mes, ano};
    }

    /**
     * Converte um LocalDate para java.util.Date usando o fuso horário padrão do sistema.
     * 
     * @param localDate o objeto LocalDate a ser convertido
     * @return o objeto Date correspondente ou null se o parâmetro for null
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


   public static LocalDate toLocalDate(Date date) {
    if (date == null) {
        return null;
    }
    return date.toInstant()
               .atZone(ZoneId.systemDefault())
               .toLocalDate();
}
}