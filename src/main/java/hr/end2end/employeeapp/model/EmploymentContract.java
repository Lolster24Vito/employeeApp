package hr.end2end.employeeapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EmploymentContract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonBackReference
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "contract_employee_id")
    private Employee contractEmployee;
    @Enumerated(EnumType.STRING)
    private EmploymentContractType contractType;
    @Column(nullable = true)
    private int contractDurationInMonths;
    @Temporal(TemporalType.DATE)
    private Date contractStart;
}
