package com.blackjack.jraoms.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
	private int id;
    private String name;
    private String email;
    private String role;
    private int departmentId;
    private int status;
 
}
