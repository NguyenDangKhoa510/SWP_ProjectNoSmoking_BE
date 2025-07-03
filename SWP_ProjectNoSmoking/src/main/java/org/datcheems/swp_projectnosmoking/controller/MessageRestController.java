package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.response.MessageRestResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.Message;
import org.datcheems.swp_projectnosmoking.repository.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MessageRestController {

    private final MessageRepository messageRepository;

    @GetMapping("/{selectionId}")
    public ResponseEntity<ResponseObject<List<MessageRestResponse>>> getChatHistory(@PathVariable Long selectionId) {
        ResponseObject<List<MessageRestResponse>> response = new ResponseObject<>();

        try {
            List<Message> messages = messageRepository.findBySelection_SelectionId(selectionId);

            List<MessageRestResponse> responseList = messages.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());

            response.setStatus("success");
            response.setMessage("Lấy lịch sử chat thành công");
            response.setData(responseList);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Internal server error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private MessageRestResponse toResponse(Message message) {
        MessageRestResponse res = new MessageRestResponse();
        res.setMessageId(message.getMessageId());
        res.setSenderType(message.getSenderType().name());
        res.setContent(message.getContent());
        res.setSentAt(message.getSentAt());
        return res;
    }


}
