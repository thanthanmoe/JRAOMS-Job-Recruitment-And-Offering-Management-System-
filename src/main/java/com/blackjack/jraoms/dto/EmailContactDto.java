package com.blackjack.jraoms.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailContactDto {
    private String email;
    private List<String> ccEmail;
    private String subject;
    private String content;
    private MultipartFile file;
}
