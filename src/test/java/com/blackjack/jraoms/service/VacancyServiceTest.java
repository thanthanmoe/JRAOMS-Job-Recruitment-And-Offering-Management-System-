package com.blackjack.jraoms.service;

import com.blackjack.jraoms.dto.VacancyDto;
import com.blackjack.jraoms.entity.*;
import com.blackjack.jraoms.repository.DepartmentRepository;
import com.blackjack.jraoms.repository.VacancyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private VacancyHistoryService vacancyHistoryService;

    @InjectMocks
    private VacancyService vacancyService;

    @Test
    void testSaveEntity() {
        Vacancy vacancy = new Vacancy();
        vacancyService.saveEntity(vacancy);
        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @Test
    public void testSaveDto() {
        VacancyDto testDto = new VacancyDto();
        testDto.setPosts("1");
        List<String> workingDays = Arrays.asList("Monday", "Wednesday", "Friday");
        testDto.setWorkingDays(workingDays);
        testDto.setDepartmentId(1);
        User testUser = new User();
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.empty());
        Mockito.doNothing().when(vacancyHistoryService).save(any());
        vacancyService.saveDto(testDto, testUser);
        verify(vacancyRepository).save(any(Vacancy.class));
        verify(vacancyHistoryService).save(any(VacancyHistory.class));
    }

    @Test
    public void testFindVacancyDtoById() {
        Department mockDepartment= mock(Department.class);
        Company mockCompany = mock(Company.class);
        when(mockDepartment.getCompany()).thenReturn(mockCompany);
        Vacancy vacancy = Vacancy.builder()
                .id(1)
                .position("Software Engineer")
                .requirePosition(2)
                .workingDay("Monday - Friday")
                .workingHours("9 AM - 5 PM")
                .salary("$60,000 - $80,000")
                .jobType("Full-time")
                .description("Exciting software development role")
                .responsibilities("Write clean code, collaborate with team")
                .requirements("Bachelor's degree in CS, Java proficiency")
                .preferences("Experience with Spring Framework")
                .startDate(LocalDate.of(2023, 9, 1))
                .endDate(LocalDate.of(2023, 9, 30))
                .status("Open")
                .department(mockDepartment)
                .daysLeft(10)
                .applyerAmount(50)
                .build();

        when(vacancyRepository.findById(1)).thenReturn(java.util.Optional.of(vacancy));
        VacancyDto result = vacancyService.findVacancyDtoById(1);
        assertEquals(1, result.getVacancyId());
        assertEquals("Software Engineer", result.getPositionName());
        verify(vacancyRepository, times(1)).findById(1);
    }

    @Test
    public void testFindByVacancy() {
        Vacancy sampleVacancy = new Vacancy();
        sampleVacancy.setId(1);
        when(vacancyRepository.findById(1)).thenReturn(Optional.of(sampleVacancy));
        Vacancy result = vacancyService.findByVacancy(1);
        assertNotNull(result);
        assertEquals(sampleVacancy, result);
        verify(vacancyRepository).findById(1);
    }

    @Test
    public void testReopenVacancyWithMatchingData() {
        VacancyDto dto = mockVacancyDto();
        User user = mockUser();
        Department department = mockDepartment();
        Vacancy currentVacancy = mockVacancy();
        currentVacancy.setId(dto.getVacancyId());
        when(vacancyRepository.findById(dto.getVacancyId())).thenReturn(Optional.of(currentVacancy));
        when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.of(department));
        int result = vacancyService.reopenVacancy(dto, user);
        verify(vacancyRepository).findById(dto.getVacancyId());
        verify(vacancyHistoryService).save(any(VacancyHistory.class));
    }

    @Test
    public void testFindById_WhenVacancyExists() {
        Vacancy sampleVacancy = new Vacancy();
        sampleVacancy.setId(1);
        when(vacancyRepository.findById(1)).thenReturn(Optional.of(sampleVacancy));
        Optional<Vacancy> result = vacancyService.findById(1);
        assertTrue(result.isPresent());
        assertEquals(sampleVacancy, result.get());
        verify(vacancyRepository).findById(1);
    }

    @Test
    public void testFindById_WhenVacancyDoesNotExist() {
        when(vacancyRepository.findById(2)).thenReturn(Optional.empty());
        Optional<Vacancy> result = vacancyService.findById(2);
        assertFalse(result.isPresent());
        verify(vacancyRepository).findById(2);
    }

    @Test
    public void testFindAllByStatus() {
        Vacancy mockVacancy1 = new Vacancy();
        mockVacancy1.setEndDate(LocalDate.now().minusDays(1));
        mockVacancy1.setStartDate(LocalDate.now().minusDays(5));
        Vacancy mockVacancy2 = new Vacancy();
        mockVacancy2.setEndDate(LocalDate.now().plusDays(2));
        mockVacancy2.setStartDate(LocalDate.now().minusDays(10));
        List<Vacancy> mockVacancies = new ArrayList<>();
        mockVacancies.add(mockVacancy1);
        mockVacancies.add(mockVacancy2);
        when(vacancyRepository.findBystatus("active")).thenReturn(mockVacancies);
        List<Vacancy> result = vacancyService.findAllByStatus("active");
        assertEquals(2, result.size());

        for (Vacancy vacancy : result) {
            if (vacancy.getEndDate().isBefore(LocalDate.now()) || vacancy.getEndDate().isEqual(LocalDate.now())) {
                assertEquals("Expired", vacancy.getStatus());
            } else {
                assertNotEquals("Expired", vacancy.getStatus());
            }
            int dayBehind = calculateDays(vacancy.getStartDate());
            assertEquals(dayBehind, vacancy.getDaysLeft());
        }
        verify(vacancyRepository, times(1)).findBystatus("active");
    }

    private int calculateDays(LocalDate startDate) {
        return (int) ChronoUnit.DAYS.between(startDate, LocalDate.now());
    }

    public static VacancyDto mockVacancyDto() {
        VacancyDto vacancyDto = new VacancyDto();

        vacancyDto.setVacancyId(1);
        vacancyDto.setPositionName("Software Developer");
        vacancyDto.setPosts("1");
        vacancyDto.setWorkingDays(Arrays.asList("Monday", "Wednesday", "Friday"));
        vacancyDto.setWorkingHourFrom("09:00 AM");
        vacancyDto.setWorkingHourTo("06:00 PM");
        vacancyDto.setSalary("50000 USD");
        vacancyDto.setJobType("Full-Time");
        vacancyDto.setRequirements("Bachelor's degree in Computer Science");
        vacancyDto.setResponsibilities("Develop and maintain software applications");
        vacancyDto.setDescription("Join our dynamic software development team!");
        vacancyDto.setPreference("Experience with Java and Spring framework");
        vacancyDto.setDepartmentId(1);
        vacancyDto.setCompanyName("TechCo");
        vacancyDto.setNumberOfApplyers(10);
        vacancyDto.setEndDate(LocalDate.of(2023, 8, 31));
        vacancyDto.setStartDate(LocalDate.of(2023, 8, 1));
        vacancyDto.setCompanyId(101);
        vacancyDto.setDepartmentName("Software Development");

        return vacancyDto;
    }

    public static Department mockDepartment() {
        Department department = new Department();

        department.setId(1);
        department.setName("Software Development");

        return department;
    }

    public static Company mockCompany() {
        Company company = new Company();

        company.setId(101);
        company.setName("TechCo");
        company.setEmail("contact@techco.com");
        company.setPhone("+1 123-456-7890");
        company.setLocation("Silicon Valley");
        company.setAbout("We are a leading tech company specializing in software development.");
        company.setLink("https://www.techco.com");

        Department department = mockDepartment();
        department.setCompany(company);

        List<Department> departments = new ArrayList<>();
        departments.add(department);
        company.setDepartments(departments);

        return company;
    }

    public static User mockUser() {
        User user = new User();

        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("secretpassword");
        user.setEnable(true);
        user.setRegisterDate(LocalDate.of(2023, 8, 1));
        user.setProfilePicture("profile.jpg");
        user.setRole(Role.ADMIN);
        user.setDepartment(mockDepartment());

        return user;
    }

    public static Vacancy mockVacancy() {
        Vacancy vacancy = new Vacancy();

        vacancy.setId(1);
        vacancy.setPosition("Software Developer");
        vacancy.setRequirePosition(2);
        vacancy.setWorkingDay("Monday - Friday");
        vacancy.setWorkingHours("9:00 AM - 6:00 PM");
        vacancy.setSalary("60000 USD");
        vacancy.setJobType("Full-Time");
        vacancy.setDescription("Join our dynamic software development team!");
        vacancy.setResponsibilities("Develop and maintain software applications");
        vacancy.setRequirements("Bachelor's degree in Computer Science");
        vacancy.setPreferences("Experience with Java and Spring framework");
        vacancy.setStartDate(LocalDate.of(2023, 8, 1));
        vacancy.setEndDate(LocalDate.of(2023, 8, 31));
        vacancy.setStatus("Open");
        vacancy.setDepartment(mockDepartment());

        vacancy.setDaysLeft(12);
        vacancy.setApplyerAmount(8);

        return vacancy;
    }
}