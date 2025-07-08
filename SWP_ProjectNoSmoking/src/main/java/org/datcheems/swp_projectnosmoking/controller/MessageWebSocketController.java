package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MessageSendRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MessageRestResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(MessageSendRequest request) {
        System.out.println("Received WS message from client: " + request);

        MessageRestResponse messageResponse = new MessageRestResponse();
        messageResponse.setSelectionId(request.getSelectionId());
        messageResponse.setSenderType(request.getSenderType());
        messageResponse.setContent(request.getContent());
        messageResponse.setSentAt(LocalDateTime.now());

        String dest = "/user/queue/messages/" + request.getSelectionId();
        messagingTemplate.convertAndSend(dest, messageResponse);
    }
}
