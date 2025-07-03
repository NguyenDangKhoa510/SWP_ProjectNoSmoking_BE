package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MessageRequest;
import org.datcheems.swp_projectnosmoking.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageSocketController {

    private final MessageService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageRequest messageRequest, Principal principal) {
        chatService.sendMessage(messageRequest, principal);
    }
}
