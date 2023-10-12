package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.dto.WebSocketDto;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WebSocketController {
    // Mapped as /app/application
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public WebSocketDto send(final WebSocketDto webSocketDto) throws Exception {
        return webSocketDto;
    }
}