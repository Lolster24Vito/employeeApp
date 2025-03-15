package hr.end2end.employeeapp.controller;

import hr.end2end.employeeapp.dto.EmployeeHomeListDto;
import hr.end2end.employeeapp.model.Employee;
import hr.end2end.employeeapp.model.EmploymentContract;
import hr.end2end.employeeapp.service.EmployeeService;
import hr.end2end.employeeapp.service.EmploymentContractService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final String uploadDir = "src/main/resources/static/images";
    private final EmploymentContractService employmentContractService;

    public EmployeeController(EmployeeService employeeService,
                              EmploymentContractService employmentContractService) {
        this.employeeService = employeeService;
        this.employmentContractService = employmentContractService;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> findAll() {

        List<Employee> allEmployees = employeeService.getAllEmployees();
        return ResponseEntity.ok(allEmployees);
    }

    @GetMapping("/employeesPage")
    public ResponseEntity<EmployeeHomeListDto> findAllInPage(@RequestParam Integer page,
                                                             @RequestParam Integer size) {
        Page<Employee> pageResult = employeeService.getAllEmployeesPaginated(page, size);
        EmployeeHomeListDto dto = new EmployeeHomeListDto(pageResult.getContent(),pageResult.getTotalPages(),page);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long employeeId) {

        Optional<Employee> employeeById = employeeService.getEmployeeById(employeeId);
        return employeeById.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }


    @PostMapping(value = "/employees", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employee> addEmployee(
            @RequestPart("employee") Employee employeeToAdd,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "employmentContract", required = false) EmploymentContract employmentContract) {

        try {
            handleEmployeeImageFile(employeeToAdd, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Employee createdEmployee = employeeService.createEmployee(employeeToAdd);
        //add contract to employee
        if (employmentContract != null) {
            employmentContract.setContractEmployee(createdEmployee);
            employmentContractService.createEmploymentContract(employmentContract);

        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PutMapping(value = "/employees/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long employeeId,
            @RequestPart("employee") Employee employeeToUpdates,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Optional<Employee> optExistingEmployee = employeeService.getEmployeeById(employeeId);
        if (!optExistingEmployee.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Employee existingEmployee = optExistingEmployee.get();
        List<EmploymentContract> existingContracts = existingEmployee.getEmploymentContracts();

        employeeToUpdates.setImagePath(existingEmployee.getImagePath());

        try {
            handleEmployeeImageFile(employeeToUpdates, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Employee updatedEmployee = employeeService.updateEmployee(employeeId, employeeToUpdates);

        List<EmploymentContract> employmentContracts = employeeToUpdates.getEmploymentContracts();
        employmentContractService.updateEmploymentContracts(employmentContracts, existingContracts, updatedEmployee);

        return ResponseEntity.ok(updatedEmployee);
    }

    private void handleEmployeeImageFile(Employee employeeToUpdates, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String filePath = saveImage(file);
            employeeToUpdates.setImagePath(filePath);
        }

    }

    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName; // Return the relative path for frontend access
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // Build the file path from the upload directory and the filename
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                // Optionally, determine the content type
                String contentType = "application/octet-stream";
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException ex) {
                    System.out.println("Could not determine file type for " + filename);
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
