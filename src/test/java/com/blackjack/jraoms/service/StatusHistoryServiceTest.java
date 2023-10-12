package com.blackjack.jraoms.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.blackjack.jraoms.entity.StatusHistory;
import com.blackjack.jraoms.repository.StatusHistoryRepository;
import com.blackjack.jraoms.service.StatusHistoryService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StatusHistoryServiceTest {

    @Mock
    private StatusHistoryRepository statusHistoryRepo;

    @InjectMocks
    private StatusHistoryService statusHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveHistoryTest() {
        StatusHistory history = new StatusHistory();
        when(statusHistoryRepo.save(any(StatusHistory.class))).thenReturn(history);

        StatusHistory savedHistory = statusHistoryService.saveHistory(history);

        assertNotNull(savedHistory);
        verify(statusHistoryRepo, times(1)).save(history);
    }
}

