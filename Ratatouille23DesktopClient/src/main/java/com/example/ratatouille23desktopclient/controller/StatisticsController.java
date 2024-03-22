package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.viewmodel.EmployeeVM;
import com.example.ratatouille23desktopclient.viewmodel.OrderVM;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.logging.Logger;

public class StatisticsController implements Initializable {

    @FXML
    private ComboBox<Employee> employeesComboBox;
    @FXML
    private ComboBox<String> timeComboBox;
    private ArrayList<String> timeValues;

    @FXML
    private BarChart<String, Integer> ordersBarChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private XYChart.Series<String, Integer> series;

    private EmployeeVM employeeVM;
    private OrderVM orderVM;

    private final Logger logger = CustomLogger.getLogger();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        logger.info("Inizializzazione fragment Statistiche.");
        employeeVM = new EmployeeVM();
        orderVM = new OrderVM();

        employeesComboBox.itemsProperty().bind(employeeVM.employeesProperty());

        initializeEmployeesComboBox();

        getEmployees();

       initializeTimeComboBox();

        logger.info("Terminata inizializzazione fragment Statistiche.");
    }

    private void initializeEmployeesComboBox(){
        employeesComboBox.setConverter(new StringConverter<Employee>() {
            @Override
            public String toString(Employee employee) {
                return employee.getName() + " " + employee.getSurname();
            }

            @Override
            public Employee fromString(String s) {
                return null;
            }
        });

        employeesComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employee>() {
            @Override
            public void changed(ObservableValue<? extends Employee> observableValue, Employee oldEmployee, Employee newEmployee) {
                if (newEmployee != null){
                    logger.info("Selezionato un dipendente nella lista. Aggiornamento dati delle statistiche.");
                    series = getData(timeComboBox.getSelectionModel().getSelectedItem(), newEmployee);
                    ordersBarChart.getData().setAll(series);
                    logger.info("Aggiornamento dati completato.");
                }
            }
        });
    }

    private void initializeTimeComboBox(){
        timeValues = new ArrayList<>();
        timeValues.add("Oggi");
        timeValues.add("Ieri");
        timeValues.add("Questa settimana");
        timeValues.add("Settimana scorsa");
        timeValues.add("Questo mese");
        timeComboBox.getItems().setAll(timeValues);
        timeComboBox.getSelectionModel().select(0);
        timeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldTime, String newTime) {
                if (newTime != null){
                    logger.info("Selezionato periodo " + newTime + ". Aggiornamento dati statistiche.");
                    series = getData(newTime, employeesComboBox.getSelectionModel().getSelectedItem());
                    ordersBarChart.getData().setAll(series);
                    logger.info("Aggiornamento dati completato.");
                }
            }
        });
    }

    private void getEmployees() {
        logger.info("Avvio procedura ottenimento dipendenti lato server.");
        employeeVM.getEmployees();
        logger.info("Terminata procedura ottenimento dipendenti lato server.");
    }

    private XYChart.Series<String, Integer> getData(String time, Employee employee){
        ZoneId zoneId = ZoneId.of("Europe/Rome");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(zoneId);

        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Ordini");

        ArrayList<LocalDate> dates = getPeriod(time);

        logger.info("Avvio procedura ottenimento statistiche lato server.");
        if (time.equals("Oggi")){
            orderVM.countOrdersOfEmployeeByDate(employee, dates.get(0), x -> series.getData().add(new XYChart.Data<>(dates.get(0).format(dateFormat), x)));
        }else if (time.equals("Ieri")){
            orderVM.countOrdersOfEmployeeByDate(employee, dates.get(0), x -> series.getData().add(new XYChart.Data<>(dates.get(0).format(dateFormat), x)));
        }else if (time.equals("Questa settimana")){
            for (LocalDate day : getPeriod(time)){
                orderVM.countOrdersOfEmployeeByDate(employee, day, x -> series.getData().add(new XYChart.Data<>(day.format(dateFormat), x)));
            }
        }else if (time.equals("Settimana scorsa")){
            for (LocalDate day : getPeriod(time)){
                orderVM.countOrdersOfEmployeeByDate(employee, day, x -> series.getData().add(new XYChart.Data<>(day.format(dateFormat), x)));
            }
        }else if (time.equals("Questo mese")){
            for (LocalDate day : getPeriod(time)){
                orderVM.countOrdersOfEmployeeByDate(employee, day, x -> series.getData().add(new XYChart.Data<>(day.format(dateFormat), x)));
            }
        }
        logger.info("Terminata procedura ottenimento statistiche lato server.");
        return series;
    }

    private ArrayList<LocalDate> getPeriod(String time){
        logger.info("Calcolo giorni contenuti nel periodo selezionato.");
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
        logger.info("Terminato calcolo giorni contenuti nel periodo selezionato.");
        return ret;
    }
}
