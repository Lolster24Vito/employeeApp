package hr.end2end.employeeapp.dto;

import hr.end2end.employeeapp.model.Employee;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeHomeListDto {
    List<Employee> employees;
    int totalPages;
    int currentPage;

    public EmployeeHomeListDto(List<Employee> employees, int totalPages, int currentPage) {
        this.employees = employees;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }
}
