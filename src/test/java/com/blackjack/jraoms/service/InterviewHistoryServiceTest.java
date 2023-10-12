package com.blackjack.jraoms.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.blackjack.jraoms.entity.InterviewHistory;
import com.blackjack.jraoms.repository.InterviewHistoryRepository;
import com.blackjack.jraoms.service.InterviewHistoryService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InterviewHistoryServiceTest {

    @Mock
    private InterviewHistoryRepository interviewHistoryRepo;

    @InjectMocks
    private InterviewHistoryService interviewHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveHistoryTest() {
        InterviewHistory history = new InterviewHistory();
        when(interviewHistoryRepo.save(any(InterviewHistory.class))).thenReturn(history);

        InterviewHistory savedHistory = interviewHistoryService.saveHistory(history);

        assertNotNull(savedHistory);
        verify(interviewHistoryRepo, times(1)).save(history);
    }
}

