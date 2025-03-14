package hr.end2end.employeeapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    @JsonProperty("firstName")
    private String firstName;
    @NotBlank
    @Column(nullable = false)
    @JsonProperty("lastName")
    private String lastName;
    @NotNull
    @Column(nullable = false)
    @JsonProperty("yearOfBirth")
    private Integer yearOfBirth;
    @JsonFormat(pattern="yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @JsonProperty("employmentStartDate")
    private Date employmentStartDate;
    @Enumerated(EnumType.ORDINAL)
    @JsonProperty("gender")
    private Gender gender;

    @Column
    @Setter
    @JsonProperty("imagePath")
    private String imagePath;

    @JsonProperty("annualVacationDaysOff")
    private Integer annualVacationDaysOff;
    @JsonProperty("daysOff")

    private Integer daysOff;
    @JsonProperty("daysOfPaidLeave")
    private Integer daysOfPaidLeave;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    @OneToMany(mappedBy="contractEmployee")
    @JsonManagedReference
    private List<EmploymentContract> employmentContracts;


}
