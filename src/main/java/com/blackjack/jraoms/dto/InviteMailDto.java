package com.blackjack.jraoms.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteMailDto {
    private String toEmail;
    private List<String> ccEmail;
    private String subject;
    private String content;

}
