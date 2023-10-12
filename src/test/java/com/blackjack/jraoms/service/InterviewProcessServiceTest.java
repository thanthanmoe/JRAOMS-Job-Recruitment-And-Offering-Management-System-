package com.blackjack.jraoms.service;

import com.blackjack.jraoms.entity.InterviewProcess;
import com.blackjack.jraoms.repository.InterviewProcessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewProcessServiceTest {

    @Mock
    private InterviewProcessRepository interviewProcessRepository;

    @InjectMocks
    private InterviewProcessService interviewProcessService;

    @Test
    public void testGetAllData() {
        List<InterviewProcess> mockInterviewProcesses = new ArrayList<>();
        mockInterviewProcesses.add(new InterviewProcess());
        mockInterviewProcesses.add(new InterviewProcess());
        mockInterviewProcesses.add(new InterviewProcess());

        when(interviewProcessRepository.findAll()).thenReturn(mockInterviewProcesses);

        List<InterviewProcess> result = interviewProcessService.getAllData();

        verify(interviewProcessRepository).findAll();
        assertEquals(3, result.size());
    }

}