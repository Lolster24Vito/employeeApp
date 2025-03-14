package hr.end2end.employeeapp.service;

import hr.end2end.employeeapp.model.Employee;
import hr.end2end.employeeapp.model.EmploymentContract;

import java.util.List;

public interface EmploymentContractService {
  EmploymentContract createEmploymentContract(EmploymentContract employmentContract);


  EmploymentContract updateEmploymentContract(EmploymentContract contract);

  void deleteEmploymentContract(Long id);


  void updateEmploymentContracts(List<EmploymentContract> updatedEmploymentContracts, List<EmploymentContract> existingContracts, Employee updatedEmployee);
}
