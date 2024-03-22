package com.example.ratatouille23desktopclient.viewmodel;

import com.example.ratatouille23desktopclient.api.CheckedAPICallback;
import com.example.ratatouille23desktopclient.api.RetrofitService;
import com.example.ratatouille23desktopclient.api.interfaces.EmployeeAPI;
import com.example.ratatouille23desktopclient.api.interfaces.StoreAPI;
import com.example.ratatouille23desktopclient.aws.auth.AuthController;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.Store;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import retrofit2.Call;
import retrofit2.Response;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeVM {
    private SimpleListProperty<Employee> employees = new SimpleListProperty<>(FXCollections.observableArrayList());
    private SimpleListProperty<Employee> filteredEmployees = new SimpleListProperty<>(FXCollections.observableArrayList());

    public SimpleListProperty<Employee> employeesProperty() {
        return this.employees;
    }

    public SimpleListProperty<Employee> filteredEmployeesProperty() {
        return this.filteredEmployees;
    }

    public void getEmployees() {
        Long storeId = Long.valueOf(RAT23Cache.getCacheInstance().get("storeId").toString());
        EmployeeAPI api = RetrofitService.getRetrofit().create(EmployeeAPI.class);
        api.getEmployeesOfStore(storeId).enqueue(new CheckedAPICallback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> employees.get().setAll(response.body()));
            }
        });
    }

    public void updateEmployee(Employee employee, String password, Runnable onUpdateSuccess, Runnable onSaveSuccess)
            throws Exception {
        AuthController authController = new AuthController();
        EmployeeAPI api = RetrofitService.getRetrofit().create(EmployeeAPI.class);

        if (employees.get().contains(employee)) {
            api.updateEmployee(employee.getId(), employee).enqueue(new CheckedAPICallback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    super.onResponse(call, response);
                    if (isSuccessfull())
                        Platform.runLater(() -> {
                            try {
                                authController.editEmployee(employee);
                                employees.get().set(employees.indexOf(employee), employee);
                                onUpdateSuccess.run();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                }
            });
        } else {
            try {
                Store currentStore = new Store();
                currentStore.setId(Long.valueOf(RAT23Cache.getCacheInstance().get("storeId").toString()));
                employee.setStore(currentStore);

                authController.createEmployee(employee, password);
                api.addEmployee(employee).enqueue(new CheckedAPICallback<Employee>() {
                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {
                        super.onResponse(call, response);
                        if (isSuccessfull())
                            Platform.runLater(() -> {
                                employees.get().add(response.body());
                                onSaveSuccess.run();
                            });
                    }
                });
            }  catch (UsernameExistsException uee) {
                throw uee;
            } catch (InvalidPasswordException ipe){
                throw ipe;
            } catch (InvalidParameterException ipe){
                throw ipe;
            } catch (Exception e){
                throw e;
            }
        }
    }

    public void deleteEmployee(Employee employee) {
        AuthController authController = new AuthController();

        EmployeeAPI api = RetrofitService.getRetrofit().create(EmployeeAPI.class);
        api.deleteEmployee(employee.getId()).enqueue(new CheckedAPICallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> {
                        try {
                            authController.deleteUser(employee.getEmail());
                            employees.get().remove(employee);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            }
        });
    }

    public void selfDelete(Employee employee, Runnable onSuccess){
        AuthController authController = new AuthController();

        EmployeeAPI api = RetrofitService.getRetrofit().create(EmployeeAPI.class);
        api.deleteEmployee(employee.getId()).enqueue(new CheckedAPICallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> {
                        try {
                            authController.deleteUser(employee.getEmail());
                            employees.get().remove(employee);
                            onSuccess.run();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            }
        });
    }

    public void filterEmployees(String term){
        filteredEmployees.set(FXCollections
                .observableArrayList(employees.get().stream()
                        .filter(employee -> employee.getName().contains(term) || employee.getSurname().contains(term))
                        .collect(Collectors.toList()))
        );
    }

    public void registerAdmin(Employee employee, Runnable onSuccess){
        EmployeeAPI api = RetrofitService.getRetrofit().create(EmployeeAPI.class);
        api.addEmployee(employee).enqueue(new CheckedAPICallback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> onSuccess.run());
            }
        });
    }
}
