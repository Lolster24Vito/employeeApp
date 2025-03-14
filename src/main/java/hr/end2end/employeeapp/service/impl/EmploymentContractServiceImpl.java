package hr.end2end.employeeapp.service.impl;

import hr.end2end.employeeapp.model.Employee;
import hr.end2end.employeeapp.model.EmploymentContract;
import hr.end2end.employeeapp.repository.EmploymentContractRepository;
import hr.end2end.employeeapp.service.EmploymentContractService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmploymentContractServiceImpl implements EmploymentContractService {
    private final EmploymentContractRepository employmentContractRepository;

    public EmploymentContractServiceImpl(EmploymentContractRepository employmentContractRepository) {
        this.employmentContractRepository = employmentContractRepository;
    }

    @Override
    public EmploymentContract createEmploymentContract(EmploymentContract employmentContract) {
        return employmentContractRepository.save(employmentContract);
    }


    @Override
    public EmploymentContract updateEmploymentContract(EmploymentContract contract) {
        // This could either update an existing contract or create a new one if needed.
        return employmentContractRepository.save(contract);
    }
    @Override
    public void deleteEmploymentContract(Long id){
        employmentContractRepository.deleteById(id);
    }

    @Override
    public void updateEmploymentContracts(List<EmploymentContract> updatedEmploymentContracts, List<EmploymentContract> existingContracts, Employee updatedEmployee) {

        if (existingContracts == null) {
            existingContracts = new ArrayList<>();
        }
        // Build a map of current contracts by their IDs for easy lookup
        Map<Long, EmploymentContract> currentContractsMap = new HashMap<>();
        for (EmploymentContract contract : existingContracts) {
            if (contract.getId() != null) {
                currentContractsMap.put(contract.getId(), contract);
            }
        }
        List<EmploymentContract> updatedContracts = new ArrayList<>();
        if (updatedEmploymentContracts != null) {
            for (EmploymentContract incoming : updatedEmploymentContracts) {
                if (incoming.getId() != null && currentContractsMap.containsKey(incoming.getId())) {
                    // Update existing contract
                    EmploymentContract existingContract = currentContractsMap.get(incoming.getId());
                    existingContract.setContractType(incoming.getContractType());
                    existingContract.setContractDurationInMonths(incoming.getContractDurationInMonths());
                    existingContract.setContractStart(incoming.getContractStart());
                    EmploymentContract savedContract = updateEmploymentContract(existingContract);
                    updatedContracts.add(savedContract);
                    // Remove the contract from the map to mark it as processed
                    currentContractsMap.remove(incoming.getId());
                } else {
                    // New contract: assign the updated employee and save
                    incoming.setContractEmployee(updatedEmployee);
                    EmploymentContract newContract = createEmploymentContract(incoming);
                    updatedContracts.add(newContract);
                }
            }
        }
        // Any contracts remaining in the currentContractsMap were not included in the update payload: delete them
        for (EmploymentContract contractToDelete : currentContractsMap.values()) {
            deleteEmploymentContract(contractToDelete.getId());
        }
        // Optionally, update the employee's contracts list with the updated ones
        updatedEmployee.setEmploymentContracts(updatedContracts);
    }

}
