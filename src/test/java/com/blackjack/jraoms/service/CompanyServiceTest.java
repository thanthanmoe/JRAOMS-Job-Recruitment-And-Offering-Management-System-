package com.blackjack.jraoms.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blackjack.jraoms.dto.AddCompanyDto;
import com.blackjack.jraoms.repository.CompanyRepository;
import com.blackjack.jraoms.repository.DepartmentRepository;
import com.blackjack.jraoms.service.CompanyService;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.blackjack.jraoms.entity.Company;
import com.blackjack.jraoms.entity.Department;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import static org.junit.jupiter.api.Assertions.assertEquals;



public class CompanyServiceTest {
	
    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DepartmentRepository departmentRepository;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    public void resetMocks() {
        Mockito.reset(companyRepository, departmentRepository);
    }
    @Test
    public void testAddCompany() {
        // Given
        AddCompanyDto addCompanyDto = new AddCompanyDto();
        addCompanyDto.setName("Test Company");
        addCompanyDto.setEmail("test@test.com");
        addCompanyDto.setPhone("1234567890");
        addCompanyDto.setLocation("Test Location");
        addCompanyDto.setAbout("About Test");
        addCompanyDto.setLink("http://test.com");
        addCompanyDto.setDepartment("Department1•Department2•Department2•  "); // Deliberately adding a duplicate and an empty name for testing

        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        companyService.addCompany(addCompanyDto);

        verify(companyRepository, times(1)).save(any(Company.class));

        verify(departmentRepository, times(2)).save(any(Department.class));
    }

    @Test
    public void testGetCompany() {
        // Create a sample DataTablesInput object (you can set appropriate values)
        DataTablesInput input = new DataTablesInput();

        // Create a sample DataTablesOutput object (you can set appropriate values)
        DataTablesOutput<Company> expectedOutput = new DataTablesOutput<>();
        expectedOutput.setData(Collections.singletonList(new Company()));

        // Mock the behavior of the companyRepository
        when(companyRepository.findAll(input)).thenReturn(expectedOutput);

        // Call the getCompany method
        DataTablesOutput<Company> result = companyService.getCompany(input);

        // Verify interactions and assertions
        verify(companyRepository).findAll(input);
        assertNotNull(result);
        assertEquals(expectedOutput.getData(), result.getData());
        // Add more assertions if needed
    }

    @Test
    public void testFindAll() {
        Company company1 = new Company();
        company1.setName("Test Company 1");
        Company company2 = new Company();
        company2.setName("Test Company 2");

        when(companyRepository.findAll()).thenReturn(Arrays.asList(company1, company2));

        List<Company> companies = companyService.findAll();

        assertEquals(2, companies.size());
        assertEquals("Test Company 1", companies.get(0).getName());
        assertEquals("Test Company 2", companies.get(1).getName());
    }

    @Test
    public void testFindById() {
        Company company = new Company();
        company.setName("Test Company");

        when(companyRepository.findById(1)).thenReturn(Optional.of(company));

        Optional<Company> result = companyService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Test Company", result.get().getName());
    }

    @Test
    public void testFindByName() {
        Company company = new Company();
        company.setName("Test Company");

        when(companyRepository.findByName("Test Company")).thenReturn(Optional.of(company));

        Optional<Company> result = companyService.findByName("Test Company");

        assertTrue(result.isPresent());
        assertEquals("Test Company", result.get().getName());
    }
    @Test
    void testUpdateCompanyWithNoErrors() {
        // Given
        Company inputEntity = new Company(); // Set any required field
        String newDepartmentList = "•HR•Finance";

        // Mocking Repository Calls
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(new Company())); // Assuming findById expects an int. If it expects Integer, use any().
        when(departmentRepository.findByCompanyId(anyInt())).thenReturn(Arrays.asList(new Department(), new Department())); // Again, adjust based on the expected type.
//        when(departmentRepository.findByCompanyIdAndNameAndEnable(anyInt(), anyString())).thenReturn(Optional.empty()); // Assuming the first argument is int and the second is String.

        // When
        boolean error = companyService.updateCompany(inputEntity, newDepartmentList);

        // Then
        assertFalse(error);
        verify(departmentRepository, times(2)).save(any());
    }

}
