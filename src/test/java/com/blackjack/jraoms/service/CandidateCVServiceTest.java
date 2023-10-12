package com.blackjack.jraoms.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.blackjack.jraoms.entity.CandidateCV;
import com.blackjack.jraoms.repository.CandidateCVRepository;
import java.util.Optional;
import static org.mockito.Mockito.*;

class CandidateCVServiceTest {

    @Mock private CandidateCVRepository candidateCVRepo;

    @InjectMocks private CandidateCVService candidateCVService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveCandidateCVTest() {
        CandidateCV cv = new CandidateCV();
        when(candidateCVRepo.save(any(CandidateCV.class))).thenReturn(cv);

        CandidateCV savedCV = candidateCVService.saveCandidateCV(cv);

        assertNotNull(savedCV);
        verify(candidateCVRepo, times(1)).save(cv);
    }

    @Test
    void findCVByIdTest() {
        Integer id = 1;
        CandidateCV cv = new CandidateCV();
        when(candidateCVRepo.findById(anyInt())).thenReturn(Optional.of(cv));

        Optional<CandidateCV> foundCV = candidateCVService.findCVById(id);

        assertTrue(foundCV.isPresent());
        assertEquals(cv, foundCV.get());
        verify(candidateCVRepo, times(1)).findById(id);
    }


}
