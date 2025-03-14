package hr.end2end.employeeapp.service;

import hr.end2end.employeeapp.model.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();

    Department createDepartment(Department department);
}
