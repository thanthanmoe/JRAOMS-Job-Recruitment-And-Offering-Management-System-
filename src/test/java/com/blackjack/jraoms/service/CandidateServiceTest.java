package com.blackjack.jraoms.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.blackjack.jraoms.dto.CandidateReportDto;
import com.blackjack.jraoms.dto.ChartsDto;
import com.blackjack.jraoms.entity.*;
import com.blackjack.jraoms.repository.CandidateRepository;
import java.text.SimpleDateFormat;


public class CandidateServiceTest {

    @Mock private CandidateRepository candidateRepo;
    @InjectMocks private CandidateService candidateService;
    @Mock private EntityManager em;
    @Mock private StoredProcedureQuery storedProcedureQuery;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCandidate() {
        Candidate candidate = new Candidate();
        candidate.setName("John");

        when(candidateRepo.save(candidate)).thenReturn(candidate);

        Candidate result = candidateService.saveCandidate(candidate);

        assertEquals(candidate.getName(), result.getName());
        verify(candidateRepo, times(1)).save(candidate);
    }

    @Test
    public void testCandidate() {
        // Create a sample Candidate object to save
        Candidate candidateToSave = new Candidate();
        candidateToSave.setName("John Doe");
        // Set other properties as needed

        // Create a sample Candidate object with an ID (after saving)
        Candidate savedCandidate = new Candidate();
        savedCandidate.setId(1);
        savedCandidate.setName("John Doe");
        // Set other properties as needed

        // Mock the behavior of the candidateRepository
        when(candidateRepo.save(candidateToSave)).thenReturn(savedCandidate);

        // Call the candidate method
        Candidate result = candidateService.candidate(candidateToSave);

        // Verify interactions and assertions
        verify(candidateRepo).save(candidateToSave);
        assertNotNull(result);
        assertEquals(savedCandidate.getId(), result.getId());
        assertEquals(savedCandidate.getName(), result.getName());
        // Add more assertions if needed
    }

    @Test
    public void testGetCandidateById() {
        Candidate candidate = new Candidate();
        candidate.setId(1);
        candidate.setName("John");

        when(candidateRepo.findById(1)).thenReturn(Optional.of(candidate));

        Optional<Candidate> result = candidateService.findCandidateById(1);

        assertTrue(result.isPresent());
        assertEquals(candidate.getId(), result.get().getId());
        verify(candidateRepo, times(1)).findById(1);
    }
    
    @Test
    public void testFindAllCandidate() {
        Candidate candidate = new Candidate();
        when(candidateRepo.findAll()).thenReturn(Arrays.asList(candidate));

        List<Candidate> result = candidateService.findAllCandidate();

        assertFalse(result.isEmpty());
        verify(candidateRepo, times(1)).findAll();
    }

    @Test
    public void testGetCandidate() {
        DataTablesInput input = new DataTablesInput();
        DataTablesOutput<Candidate> output = new DataTablesOutput<>();
        when(candidateRepo.findAll(input)).thenReturn(output);

        DataTablesOutput<Candidate> result = candidateService.getCandidate(input);

        assertNotNull(result);
        verify(candidateRepo, times(1)).findAll(input);
    }

    @Test
    public void testGetAllCandidatesAndVacancies() {
        CandidateReportDto dto = new CandidateReportDto();
        // set properties for dto if required

        when(candidateRepo.getAllCandidatesAndVacancies()).thenReturn(Arrays.asList(dto));

        List<CandidateReportDto> result = candidateService.getAllCandidatesAndVacancies();

        assertFalse(result.isEmpty());
        verify(candidateRepo, times(1)).getAllCandidatesAndVacancies();
    }

    @Test
    public void testFetchCandidatesAndVacancies() throws java.text.ParseException {
        // Correctly creating Date objects:
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        

		when(em.createStoredProcedureQuery("sp_getCandidatesAndVacancies")).thenReturn(storedProcedureQuery);

		// Mock data that matches the constructor of CandidateReportDto
		when(storedProcedureQuery.getResultList()).thenReturn(Arrays.asList(new Object[]{
			    new Object[]{ // Array of objects representing a row
			        1, "John", new java.sql.Date(System.currentTimeMillis()), "PASSED", "2005-08-01", "male", "094428909",
			        "moelay@gmail.com", "B-Tech", "Java", "English","Java Developer", "Mid Level", "Java", "3", "300000"
			    }
			}));
		  List<CandidateReportDto> result = candidateService.fetchCandidatesAndVacancies(startDate, endDate);

	        assertFalse(result.isEmpty());
    }

    @Test
    public void testFetchCandidatesByStatus() {
        String status = "PASSED";

        when(em.createStoredProcedureQuery("sp_getCandidatesByStatus")).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getResultList()).thenReturn(Arrays.asList(new Object[]{
			    new Object[]{ // Array of objects representing a row
			        1, "John", new java.sql.Date(System.currentTimeMillis()), "PASSED", "2005-08-01", "male", "094428909",
			        "moelay@gmail.com", "B-Tech", "Java", "English","Java Developer", "Mid Level", "Java", "3", "300000","2005-08-01"
			    }
			}));
		  List<CandidateReportDto> result = candidateService.fetchCandidatesByStatus(status);

	        assertFalse(result.isEmpty());
        
    }

    @Test
    public void testFindAllCandidateByVacancyId() {
        int id = 1;
        Candidate candidate = new Candidate();
        when(candidateRepo.findByVacancyId(id)).thenReturn(Arrays.asList(candidate));

        List<Candidate> result = candidateService.findAllCandidateByVacancyId(id);

        assertFalse(result.isEmpty());
        verify(candidateRepo, times(1)).findByVacancyId(id);
    }

    @Test
    public void testFetchCountByVacancy_id() {
        ChartsDto dto = new ChartsDto();
        // set properties for dto if required

        when(em.createStoredProcedureQuery("get_countByVacancy_id")).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getResultList()).thenReturn(Arrays.asList(new Object[]{
			    new Object[]{ // Array of objects representing a row
			        1, "John"
			    }
			}));
		  List<ChartsDto> result = candidateService.fetchCountByVacancy_id();

	        assertFalse(result.isEmpty());
    }
    @Test
    public void testFindByCVStatus() {
        // Arrange
        String testCvStatus = "PASSED";
        List<Candidate> expectedCandidates = Arrays.asList(new Candidate(), new Candidate());
        when(candidateRepo.findByCvStatus(testCvStatus)).thenReturn(expectedCandidates);

        // Act
        List<Candidate> result = candidateService.findByCVStatus(testCvStatus);

        // Assert
        assertEquals(expectedCandidates, result);
        verify(candidateRepo, times(1)).findByCvStatus(testCvStatus);
    }
    @Test
    public void testFetchCandidatesByVacancyId() {
        Integer vacancyId = 1;

        // mock the creation of the StoredProcedureQuery
        when(em.createStoredProcedureQuery("sp_getCandidatesByVacancyId")).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getResultList()).thenReturn(Arrays.asList(new Object[]{
                new Object[]{ // Array of objects representing a row
                        1, "John", new java.sql.Date(System.currentTimeMillis()), "PASSED", "2005-08-01", "male", "094428909",
                        "moelay@gmail.com", "B-Tech", "Java", "English","Java Developer", "Mid Level", "Java", "3", "300000","2005-08-01"
                }
        }));



        List<CandidateReportDto> result = candidateService.fetchCandidatesByVacancyId(vacancyId);


        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        CandidateReportDto candidate = result.get(0);
        assertEquals(Integer.valueOf(1), candidate.getId());  // replace with appropriate getter method
        assertEquals("John", candidate.getName());        // replace with appropriate getter method

    }
}



