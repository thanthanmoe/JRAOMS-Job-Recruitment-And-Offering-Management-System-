package com.blackjack.jraoms.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDto {
    private String to;
    private List<String> cc;
    private String subject;
    private String interviewType;
    private String interviewStage;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String content;
    private MultipartFile file;
}