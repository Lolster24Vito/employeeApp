package hr.end2end.employeeapp.repository;

import hr.end2end.employeeapp.model.EmploymentContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmploymentContractRepository extends JpaRepository<EmploymentContract,Long> {
}
