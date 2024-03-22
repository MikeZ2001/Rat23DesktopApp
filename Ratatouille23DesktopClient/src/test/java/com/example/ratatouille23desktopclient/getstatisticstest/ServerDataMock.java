package com.example.ratatouille23desktopclient.getstatisticstest;

import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.Order;
import javafx.collections.FXCollections;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;

/**Questa classe serve solo a simulare i dati presenti nel database lato server.
 * Tutti i dati vengono generati nel momento in cui un oggetto di questo mock viene inizializzato tramite costruttore (e metodi di supporto).
 * Lo scopo è valutare solo il corretto funzionamento strutturale del metodo per la riproposizione dei dati recuperati nel grafico definito da XYChart.
 * Dati fittizi utilizzati:
 *      1. Caratteristiche degli impiegati presenti nel database
 *          - I campi dichiarati per ciascun impiegato per semplicità saranno id (generato in modo sequenziale) ed email (gli unici essenziali all'equals)
 *          - Impiegato che in questo mese ha evaso 10 ordini ogni giorno: employeeTen
 *          - Impiegato che in questo mese ha evaso 0 ordini ogni giorno: employeeLazy
 *      2. Caratteristiche degli ordini evasi dagli impiegati del punto 1 presenti nel database
 *          - Tutti gli ordini generati devono avere status: PAYED per essere considerati ai fini delle statistiche
 *          - Tutti gli ordini generati hanno id sequenziali per distinguersi tra loro come nel database
 *          - Tutti gli ordini generati hanno solo uno degli employee del punto 1 come "employeesOfTheOrder"
 *          - Essendo irrilevanti ai fini del test omettiamo prodotti dell'ordine, note aggiuntive, totale di spesa e tutti gli altri campi che definiscono un ordine reale
 *
 * Sempre considerando lo scopo del test, presupporremo che anche il metodo di appoggio per calcolare le date in base alla stringa "time" funzioni correttamente.
 */
public class ServerDataMock {
    private final ZoneId zoneId = ZoneId.of("Europe/Rome");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(zoneId);
    private ArrayList<Employee> employees; //lista fittizia di impiegati nel database
    private Employee employeeTen, employeeLazy;
    private ArrayList<Order> ordersData; //lista di ordini fittizi che popolano il database

    public ServerDataMock(){
        generateFakeData();
    }

    private void generateFakeData(){
        this.employees = generateEmployeesData();
        this.ordersData = generateOrdersData();
    }

    private ArrayList<Employee> generateEmployeesData(){
        ArrayList<Employee> employees = new ArrayList<>();

        int id = 0;

        employeeTen = new Employee();
        employeeTen.setId(++id);
        employeeTen.setEmail("employeeTen@gmail.com");
        employees.add(employeeTen);

        employeeLazy = new Employee();
        employeeLazy.setId(++id); //1
        employeeLazy.setEmail("employeeLazy@gmail.com");
        employees.add(employeeLazy);

        return employees;
    }

    private ArrayList<Order> generateOrdersData() {
        ArrayList<Order> orders = new ArrayList<>();
        int id = 0;
        ArrayList<Employee> employees1 = new ArrayList<>();

        //Genera ordini di employeeTen
        for(LocalDate day: getPeriod("Questo mese")){
            for (int i = 0; i < 10; i++){
                Order order = new Order();
                order.setId(++id);
                employees1.clear();
                employees1.add(employeeTen);
                order.setEmployeesOfTheOrder(FXCollections.observableArrayList(employees1));
                order.setStatus(Order.Status.PAYED);
                order.setDate(day.format(dateFormat));
                orders.add(order);
            }
        }

        //Genera ordini di employeeLazy

        return orders;
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

    //Simula la query al db
    public int countOrdersOfEmployeeByDate(Employee employee, LocalDate day){
        int ret = 0;
        if (employees.contains(employee)){
            for(Order order: ordersData){
                if (order.getEmployeesOfTheOrder().contains(employee) && order.getDate().equals(day.format(dateFormat)) && order.getStatus().equals(Order.Status.PAYED)) {
                    ret++;
                }
            }
        }
        return ret;
    }
}
