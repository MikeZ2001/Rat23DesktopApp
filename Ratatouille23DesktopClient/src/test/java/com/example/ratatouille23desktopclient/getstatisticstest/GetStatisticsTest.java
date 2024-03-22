package com.example.ratatouille23desktopclient.getstatisticstest;

import com.example.ratatouille23desktopclient.model.Employee;
import javafx.scene.chart.XYChart;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Test con approccio Black-Box per testare il comportamento del metodo che ricava e elabora le statistiche
 * degli ordini e dei dipendenti in base al periodo temporale scelto. Più precisamente la valutazione avviene principalmente
 * in funzione di input e output (per cui Black-box), ma gli input dei casi di test sono stati pensati in funzione
 * del comportamento atteso dal metodo (per cui anche a tratti White-Box).
 * Lo scopo è valutare che il metodo segua correttamente le logiche previste e quindi soddisfi i requisiti.
 * Per farlo abbiamo un mock che simula le chiamate a server con dei dati fake prestabiliti in ServerDataMock.
 * I dati sono stati generati in modo da coprire la maggior parte possibile di casi:
 * Gli impiegati:
 *      - employeeTen ha lavorato tutti i giorni in questo mese evadendo ogni giorno 10 ordini
 *      - employeeLazy non ha lavorato tutto il mese
 *      - Al di fuori del mese corrente nessuno degli impiegati ha lavorato
 * I criteri di testing sono:
 *      - La dimensione dei risultati attesi e ottenuti coincide (ad esempio entrambi contengono 31 dati per i 31 giorni di un mese)
 *      - Il numero di ordini evasi nel periodo di tempo stabilito previsti e ottenuti coincide (ad esempio un impiegato che ha evaso 10 ordini oggi risulta averne
 *          evasi effettivamente 10)
 *      - La singola data di ciascun dato attesa e ottenuta coincide (ad esempio non solo le statistiche dell'impiegato coincidono, ma coincidono nello specifico per
 *          la data 23/07/19)
 * I casi di test previsti sono quindi le combinazioni di "tipologia" di impiegato (vedi sopra) e i periodi di tempo disponibili nel software.
 */
public class GetStatisticsTest {
    private GetStatisticsMock mock;
    private Employee employeeTen, employeeLazy; //Impiegati da usare nei test presenti nel db
    private final String TODAY = "Oggi";
    private final String YESTERDAY = "Ieri";
    private final String THIS_WEEK = "Questa settimana"; //Intesa come LUN-DOM
    private final String LAST_WEEK = "Settimana scorsa";
    private final String THIS_MONTH = "Questo mese";

    private ZoneId zoneId = ZoneId.of("Europe/Rome");
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(zoneId);

    @Before
    public void setUp(){
        mock = new GetStatisticsMock();

        int id = 0;

        employeeTen = new Employee();
        employeeTen.setId(++id); //0
        employeeTen.setEmail("employeeTen@gmail.com");

        employeeLazy = new Employee();
        employeeLazy.setId(++id); //1
        employeeLazy.setEmail("employeeLazy@gmail.com");
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

    //L'impiegato che ha evaso 10 ordini tutti i giorni del mese dovrebbe averne evasi 10 oggi
    @Test
    public void employeeTenToday(){
        int expectedOrdersPerDay = 10;
        Employee selectedEmployee = employeeTen;
        String time = TODAY;

        //Genera XYChart.Data attesi
        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();
        expected.add(new XYChart.Data<>(getPeriod(time).get(0).format(dateFormat), expectedOrdersPerDay));

        //Ottieni XYChart.Data effettivi
        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che ha evaso 10 ordini tutti i giorni del mese dovrebbe averne evasi 10 ieri (a meno che ieri non sia il giorno di un altro mese)
    @Test
    public void employeeTenYesterday(){
        int expectedOrdersPerDay = 10;
        Employee selectedEmployee = employeeTen;
        String time = YESTERDAY;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();

        LocalDate yesterday = getPeriod(time).get(0);
        LocalDate today = getPeriod(TODAY).get(0);

        if (yesterday.getMonth().equals(today.getMonth()) && yesterday.getYear() == today.getYear()){
            //Ieri rientra nel mese attuale
            expected.add(new XYChart.Data<>(yesterday.format(dateFormat), expectedOrdersPerDay));
        }else{
            //Ieri rientra nel mese scorso
            expected.add(new XYChart.Data<>(yesterday.format(dateFormat), 0));
        }

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che ogni giorno ha evaso 10 ordini per tutto questo mese dovrebbe aver evaso 10 ordini per ogni giorno della settimana corrente incluso nel mese corrente
    @Test
    public void employeeTenThisWeek(){
        int expectedOrdersPerDay = 10;
        Employee selectedEmployee = employeeTen;
        String time = THIS_WEEK;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();

        List<LocalDate> thisWeek = getPeriod(time);
        LocalDate today = getPeriod(TODAY).get(0);

        for (LocalDate day: thisWeek){
            if (day.getMonth().equals(today.getMonth()) && day.getYear() == today.getYear()){
                //Giorno della settimana che rientra nel mese in corso
                expected.add(new XYChart.Data<>(day.format(dateFormat), expectedOrdersPerDay));
            }else{
                //Giorno della settimana che rientra nel mese precedente/successivo
                expected.add(new XYChart.Data<>(day.format(dateFormat), 0));
            }
        }

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che ogni giorno ha evaso 10 ordini per tutto questo mese ci attendiamo abbia evaso 10 ordini per ogni giorno della settimana scorsa incluso nel mese corrente
    @Test
    public void employeeTenLastWeek(){
        int expectedOrdersPerDay = 10;
        Employee selectedEmployee = employeeTen;
        String time = LAST_WEEK;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();

        List<LocalDate> thisWeek = getPeriod(time);
        LocalDate today = getPeriod(TODAY).get(0);

        for (LocalDate day: thisWeek){
            if (day.getMonth().equals(today.getMonth()) && day.getYear() == today.getYear()){
                //Giorno della settimana che rientra nel mese in corso
                expected.add(new XYChart.Data<>(day.format(dateFormat), expectedOrdersPerDay));
            }else{
                //Giorno della settimana che rientra nel mese precedente/successivo
                expected.add(new XYChart.Data<>(day.format(dateFormat), 0));
            }
        }

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che ogni giorno ha evaso 10 ordini per tutto questo mese dovrebbe aver evaso 10 ordini ogni giorno del mese corrente
    @Test
    public void employeeTenThisMonth(){
        int expectedOrdersPerDay = 10;
        Employee selectedEmployee = employeeTen;
        String time = THIS_MONTH;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();

        for (LocalDate day: getPeriod(time)){
            expected.add(new XYChart.Data<>(day.format(dateFormat), expectedOrdersPerDay));
        }

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che non ha mai lavorato tutto il mese ci attendiamo abbia evaso 0 ordini oggi
    @Test
    public void employeeLazyToday(){
        int expectedOrdersPerDay = 0;
        Employee selectedEmployee = employeeLazy;
        String time = TODAY;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();
        expected.add(new XYChart.Data<>(getPeriod(time).get(0).format(dateFormat), expectedOrdersPerDay));

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che non ha mai lavorato questo mese dovrebbe aver evaso 0 ordini ieri sia che si tratti del mese corrente che del mese scorso
    @Test
    public void employeeLazyYesterday(){
        int expectedOrdersPerDay = 0;
        Employee selectedEmployee = employeeLazy;
        String time = YESTERDAY;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();

        LocalDate yesterday = getPeriod(time).get(0);
        LocalDate today = getPeriod(TODAY).get(0);

        if (yesterday.getMonth().equals(today.getMonth()) && yesterday.getYear() == today.getYear()){
            //Ieri rientra nel mese attuale
            expected.add(new XYChart.Data<>(yesterday.format(dateFormat), expectedOrdersPerDay));
        }else{
            //Ieri rientra nel mese scorso
            expected.add(new XYChart.Data<>(yesterday.format(dateFormat), 0));
        }

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che non ha mai lavorato questo mese dovrebbe aver evaso 0 ordini ogni giorno di questa settimana
    //(anche qualora alcuni giorni cadessero nel mese scorso/successivo)
    @Test
    public void employeeLazyThisWeek(){
        int expectedOrdersPerDay = 0;
        Employee selectedEmployee = employeeLazy;
        String time = THIS_WEEK;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();

        List<LocalDate> thisWeek = getPeriod(time);
        LocalDate today = getPeriod(TODAY).get(0);

        for (LocalDate day: thisWeek){
            if (day.getMonth().equals(today.getMonth()) && day.getYear() == today.getYear()){
                //Giorno della settimana che rientra nel mese in corso
                expected.add(new XYChart.Data<>(day.format(dateFormat), expectedOrdersPerDay));
            }else{
                //Giorno della settimana che rientra nel mese precedente/successivo
                expected.add(new XYChart.Data<>(day.format(dateFormat), 0));
            }
        }

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che non ha mai lavorato tutto il mese dovrebbe aver evaso 0 ordini ogni giorno della settimana
    //scorsa (anche qualora dei giorni cadessero nel mese precedente/successivo)
    @Test
    public void employeeLazyLastWeek(){
        int expectedOrdersPerDay = 0;
        Employee selectedEmployee = employeeLazy;
        String time = LAST_WEEK;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();

        List<LocalDate> thisWeek = getPeriod(time);
        LocalDate today = getPeriod(TODAY).get(0);

        for (LocalDate day: thisWeek){
            if (day.getMonth().equals(today.getMonth()) && day.getYear() == today.getYear()){
                //Giorno della settimana che rientra nel mese in corso
                expected.add(new XYChart.Data<>(day.format(dateFormat), expectedOrdersPerDay));
            }else{
                //Giorno della settimana che rientra nel mese precedente/successivo
                expected.add(new XYChart.Data<>(day.format(dateFormat), 0));
            }
        }

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }

    //L'impiegato che non ha mai lavorato questo mese dovrebbe aver evaso 0 ordini ogni giorno del mese
    @Test
    public void employeeLazyThisMonth(){
        int expectedOrdersPerDay = 0;
        Employee selectedEmployee = employeeLazy;
        String time = THIS_MONTH;

        List<XYChart.Data<String,Integer>> expected = new ArrayList<>();

        for (LocalDate day: getPeriod(time)){
            expected.add(new XYChart.Data<>(day.format(dateFormat), expectedOrdersPerDay));
        }

        XYChart.Series<String, Integer> res = mock.getData(time, selectedEmployee);
        List<XYChart.Data<String, Integer>> data = res.getData();

        Assert.assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++){
            Assert.assertEquals(expected.get(i).getXValue(),data.get(i).getXValue());
            Assert.assertEquals(expected.get(i).getYValue(),data.get(i).getYValue());
        }
    }
}
