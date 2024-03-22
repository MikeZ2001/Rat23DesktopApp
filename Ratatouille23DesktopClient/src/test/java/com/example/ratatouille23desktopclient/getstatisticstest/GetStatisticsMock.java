package com.example.ratatouille23desktopclient.getstatisticstest;

import com.example.ratatouille23desktopclient.model.Employee;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Function;

/**
 * Classe con effettivo metodo da testare.
 * Per i dati ricevuti rifarsi a ServerDataMock.
 */
public class GetStatisticsMock {
    private ServerDataMock serverDataMock;

    public GetStatisticsMock(){
        serverDataMock = new ServerDataMock(); //Inizializza i dati nel database fittizio
    }

    //Metodo da testare
    public XYChart.Series<String, Integer> getData(String time, Employee employee){
        ZoneId zoneId = ZoneId.of("Europe/Rome");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(zoneId);

        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Ordini");

        ArrayList<LocalDate> dates = getPeriod(time);

        //logger.info("Avvio procedura ottenimento statistiche lato server.");
        if (time.equals("Oggi")){
            countOrdersOfEmployeeByDate(employee, dates.get(0), x -> series.getData().add(new XYChart.Data<>(dates.get(0).format(dateFormat), x)));
        }else if (time.equals("Ieri")){
            countOrdersOfEmployeeByDate(employee, dates.get(0), x -> series.getData().add(new XYChart.Data<>(dates.get(0).format(dateFormat), x)));
        }else if (time.equals("Questa settimana")){
            for (LocalDate day : getPeriod(time)){
                countOrdersOfEmployeeByDate(employee, day, x -> series.getData().add(new XYChart.Data<>(day.format(dateFormat), x)));
            }
        }else if (time.equals("Settimana scorsa")){
            for (LocalDate day : getPeriod(time)){
                countOrdersOfEmployeeByDate(employee, day, x -> series.getData().add(new XYChart.Data<>(day.format(dateFormat), x)));
            }
        }else if (time.equals("Questo mese")){
            for (LocalDate day : getPeriod(time)){
                countOrdersOfEmployeeByDate(employee, day, x -> series.getData().add(new XYChart.Data<>(day.format(dateFormat), x)));
            }
        }
        //logger.info("Terminata procedura ottenimento statistiche lato server.");
        return series;
    }

    //Simula la richiesta di OrderVM al server
    private void countOrdersOfEmployeeByDate(Employee employee, LocalDate localDate, Function<Integer, Boolean> onSuccess) {
        onSuccess.apply(serverDataMock.countOrdersOfEmployeeByDate(employee,localDate));
    }

    //Metodo di supporto per generare facilmente i periodi di tempo di appartenenza dei dati. Si suppone funzionante correttamente ai fini del test.
    private ArrayList<LocalDate> getPeriod(String time){
        //logger.info("Calcolo giorni contenuti nel periodo selezionato.");
        ArrayList<LocalDate> ret = new ArrayList<>();
        ZoneId zoneId = ZoneId.of("Europe/Rome");
        if (time.equals("Oggi")){
            LocalDate today = LocalDate.now(zoneId);
            ret.add(today);
        }else if (time.equals("Ieri")){
            LocalDate yesterday = LocalDate.now(zoneId).minus(1, ChronoUnit.DAYS);
            ret.add(yesterday);
        }else if (time.equals("Questa settimana")){
            LocalDate startWeek = LocalDate.now(zoneId).with(TemporalAdjusters.previousOrSame(WeekFields.of(Locale.ITALY).getFirstDayOfWeek()));
            ret.add(startWeek);
            for (int i = 1; i < 7; i++){
                ret.add(startWeek.plus(i, ChronoUnit.DAYS));
            }
        }else if (time.equals("Settimana scorsa")){
            LocalDate startWeek = LocalDate.now(zoneId).minus(7,ChronoUnit.DAYS).with(TemporalAdjusters.previousOrSame(WeekFields.of(Locale.ITALY).getFirstDayOfWeek()));
            ret.add(startWeek);
            for (int i = 1; i < 7; i++){
                ret.add(startWeek.plus(i, ChronoUnit.DAYS));
            }
        }else if (time.equals("Questo mese")){
            LocalDate startMonth = LocalDate.now(zoneId).withDayOfMonth(1);
            LocalDate endMonth = LocalDate.now(zoneId).withDayOfMonth(LocalDate.now(zoneId).getMonth().length(LocalDate.now(zoneId).isLeapYear()));
            LocalDate day = startMonth;
            ret.add(startMonth);
            while (day.plus(1, ChronoUnit.DAYS).isBefore(endMonth) || day.plus(1, ChronoUnit.DAYS).isEqual(endMonth)){
                day = day.plus(1, ChronoUnit.DAYS);
                ret.add(day);
            }
        }
        //logger.info("Terminato calcolo giorni contenuti nel periodo selezionato.");
        return ret;
    }
}
