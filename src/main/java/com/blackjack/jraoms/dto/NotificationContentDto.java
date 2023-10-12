package com.blackjack.jraoms.dto;

import com.blackjack.jraoms.entity.Notification;
import com.blackjack.jraoms.entity.Vacancy;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationContentDto {
    private Vacancy vacancy;
    private List<Notification> notifications;
}
