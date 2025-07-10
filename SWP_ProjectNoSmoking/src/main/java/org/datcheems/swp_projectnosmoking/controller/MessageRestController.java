package org.datcheems.swp_projectnosmoking.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MessageSendRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MessageRestResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.Message;
import org.datcheems.swp_projectnosmoking.entity.MemberCoachSelection;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.repository.MessageRepository;
import org.datcheems.swp_projectnosmoking.repository.MemberCoachSelectionRepository;
import org.datcheems.swp_projectnosmoking.repository.MemberRepository;
import org.datcheems.swp_projectnosmoking.repository.CoachRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequiredArgsConstructor
public class MessageRestController {

    private final MessageRepository messageRepository;
    private final MemberCoachSelectionRepository selectionRepository;
    private final MemberRepository memberRepository;
    private final CoachRepository coachRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/history/{selectionId}")
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
            e.printStackTrace();
            response.setStatus("error");
            response.setMessage("Internal server error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/send")
    public ResponseEntity<ResponseObject<MessageRestResponse>> sendMessage(@RequestBody MessageSendRequest request) {
        ResponseObject<MessageRestResponse> response = new ResponseObject<>();
        try {
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                response.setStatus("error");
                response.setMessage("Nội dung tin nhắn không được để trống");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            MemberCoachSelection selection = null;

            if (request.getSelectionId() != null) {
                Optional<MemberCoachSelection> selectionOpt = selectionRepository.findById(request.getSelectionId());
                if (selectionOpt.isPresent()) {
                    selection = selectionOpt.get();
                }
            }

            if (selection == null && request.getUserId() != null && request.getCoachId() != null) {
                Optional<Member> memberOpt = memberRepository.findByUserId(request.getUserId());
                Optional<Coach> coachOpt = coachRepository.findById(request.getCoachId());

                if (memberOpt.isEmpty()) {
                    response.setStatus("error");
                    response.setMessage("Không tìm thấy member với userId: " + request.getUserId());
                    response.setData(null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                if (coachOpt.isEmpty()) {
                    response.setStatus("error");
                    response.setMessage("Không tìm thấy coach với coachId: " + request.getCoachId());
                    response.setData(null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Member member = memberOpt.get();
                Coach coach = coachOpt.get();

                Optional<MemberCoachSelection> existingSelection = selectionRepository.findByMemberAndCoach(member, coach);
                if (existingSelection.isPresent()) {
                    selection = existingSelection.get();
                } else {
                    MemberCoachSelection newSelection = new MemberCoachSelection();
                    newSelection.setMember(member);
                    newSelection.setCoach(coach);
                    newSelection.setSelectedAt(LocalDateTime.now());

                    selection = selectionRepository.save(newSelection);
                }
            }

            if (selection == null) {
                response.setStatus("error");
                response.setMessage("Không thể xác định selection. Cần có selectionId hoặc (userId + coachId)");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            Message message = new Message();
            message.setSelection(selection);
            message.setContent(request.getContent().trim());
            message.setSentAt(LocalDateTime.now());
            message.setIsRead(false);

            if ("USER".equalsIgnoreCase(request.getSenderType())) {
                message.setSenderType(Message.SenderType.MEMBER);
            } else if ("COACH".equalsIgnoreCase(request.getSenderType())) {
                message.setSenderType(Message.SenderType.COACH);
            } else {
                response.setStatus("error");
                response.setMessage("SenderType không hợp lệ. Phải là USER hoặc COACH");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Message savedMessage = messageRepository.save(message);

            MessageRestResponse messageResponse = toResponse(savedMessage);
            try {
                String destination = "/user/queue/messages/" + selection.getSelectionId();
                messagingTemplate.convertAndSend(destination, messageResponse);
                messagingTemplate.convertAndSend("/user/queue/messages/global", messageResponse);
                System.out.println("Broadcasted message to: " + destination);
            } catch (Exception e) {
                System.err.println("Failed to broadcast message: " + e.getMessage());
                // Không throw error vì message đã được lưu thành công
            }

            response.setStatus("success");
            response.setMessage("Gửi tin nhắn thành công");
            response.setData(messageResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus("error");
            response.setMessage("Internal server error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    @PutMapping("/mark-read/{selectionId}")
    public ResponseEntity<ResponseObject<String>> markMessagesAsRead(@PathVariable Long selectionId) {
        ResponseObject<String> response = new ResponseObject<>();
        try {
            int updatedRows = messageRepository.markAllMessagesAsRead(selectionId);
            response.setStatus("success");
            response.setMessage("Đã mark read " + updatedRows + " tin nhắn.");
            response.setData("Ok");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus("error");
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    private MessageRestResponse toResponse(Message message) {
        MessageRestResponse res = new MessageRestResponse();
        res.setMessageId(message.getMessageId());
        res.setSelectionId(message.getSelection().getSelectionId());
        res.setSenderType(message.getSenderType().name());
        res.setContent(message.getContent());
        res.setSentAt(message.getSentAt());
        res.setIsRead(message.getIsRead());
        return res;
    }

}
