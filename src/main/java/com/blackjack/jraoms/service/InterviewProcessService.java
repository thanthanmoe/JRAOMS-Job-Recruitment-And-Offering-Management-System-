package com.blackjack.jraoms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import com.blackjack.jraoms.entity.InterviewProcess;
import com.blackjack.jraoms.repository.InterviewProcessRepository;

@Service
public class InterviewProcessService {

    @Autowired
    private InterviewProcessRepository interviewProcessRepository;

    public List<InterviewProcess> getAllData() {
        return interviewProcessRepository.findAll();
    }
}

