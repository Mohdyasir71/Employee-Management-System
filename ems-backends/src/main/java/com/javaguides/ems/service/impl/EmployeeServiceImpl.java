package com.javaguides.ems.service.impl;

import com.javaguides.ems.dto.EmployeeDto;
import com.javaguides.ems.entity.Department;
import com.javaguides.ems.entity.Employee;
import com.javaguides.ems.exception.ResourceNotFoundException;
import com.javaguides.ems.mapper.EmployeeMapper;
import com.javaguides.ems.repository.DepartmentRepository;
import com.javaguides.ems.repository.EmployeeRepository;
import com.javaguides.ems.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        // Map DTO to Employee
        Employee employee = EmployeeMapper.maptoEmployee(employeeDto);

        // Ensure that departmentId is provided in employeeDto and is valid
        if (employeeDto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department is not exists with id: " + employeeDto.getDepartmentId()));

            employee.setDepartment(department);
        } else {
            throw new ResourceNotFoundException("Department ID is missing in the request.");
        }

        // Save employee and map back to DTO
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee is not exists with given id: " + employeeId));

        // Check if employee's department is null before accessing
        if (employee.getDepartment() == null) {
            throw new ResourceNotFoundException("Employee does not have an assigned department.");
        }

        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(EmployeeMapper::mapToEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee is not exists with given id: " + employeeId));

        // Update employee fields from DTO
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());

        // Ensure department ID is provided and valid
        if (updatedEmployee.getDepartmentId() != null) {
            Department department = departmentRepository.findById(updatedEmployee.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department is not exists with id: " + updatedEmployee.getDepartmentId()));

            employee.setDepartment(department);
        } else {
            throw new ResourceNotFoundException("Department ID is missing in the request.");
        }

        // Save updated employee and return the DTO
        Employee updatedEmployeeObj = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(updatedEmployeeObj);
    }

    @Override
    public void deleteEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee is not exists with given id: " + employeeId));

        // Ensure employee has a department before deleting
        if (employee.getDepartment() == null) {
            throw new ResourceNotFoundException("Employee does not have an assigned department.");
        }

        employeeRepository.deleteById(employeeId);
    }
}
