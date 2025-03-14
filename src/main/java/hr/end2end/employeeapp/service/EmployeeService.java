package hr.end2end.employeeapp.service;

import hr.end2end.employeeapp.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    //Create,read,update,details
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Long id);
    Employee createEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employee);
    void deleteEmployee(Long id);
}
