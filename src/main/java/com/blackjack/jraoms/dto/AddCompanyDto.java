package com.blackjack.jraoms.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCompanyDto {
    private String name;
    private String email;
    private String phone;
    private String location;
    private String about;
    private String department;
    private String link;
}
