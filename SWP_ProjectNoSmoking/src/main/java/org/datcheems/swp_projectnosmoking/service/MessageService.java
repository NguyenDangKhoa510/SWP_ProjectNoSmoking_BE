package org.datcheems.swp_projectnosmoking.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.datcheems.swp_projectnosmoking.dto.request.MessageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MessageResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MessageService {

    UserRepository userRepository;
    MemberRepository memberRepository;
    CoachRepository coachRepository;
    MemberCoachSelectionRepository memberCoachSelectionRepository;
    MessageRepository messageRepository;
    SimpMessagingTemplate messagingTemplate;

    public void sendMessage(MessageRequest request, Principal principal) {

        // Lấy username đang đăng nhập
//        String senderUsername = principal.getName();
        String senderUsername = (principal != null) ? principal.getName() : "Unknown";
        System.out.println("User gửi: " + senderUsername);
        User senderUser = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender user not found"));

        // Lấy User của người nhận
        User receiverUser = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver user not found"));

        boolean senderIsMember = senderUser.getRoles().stream()
                .anyMatch(r -> r.getName() == Role.RoleName.MEMBER);

        boolean senderIsCoach = senderUser.getRoles().stream()
                .anyMatch(r -> r.getName() == Role.RoleName.COACH);

        Member member;
        Coach coach;

        if (senderIsMember) {
            member = memberRepository.findByUser(senderUser)
                    .orElseThrow(() -> new RuntimeException("Member not found"));
            coach = coachRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Coach not found"));
        } else if (senderIsCoach) {
            coach = coachRepository.findByUser(senderUser)
                    .orElseThrow(() -> new RuntimeException("Coach not found"));
            member = memberRepository.findByUser(receiverUser)
                    .orElseThrow(() -> new RuntimeException("Member not found"));
        } else {
            throw new RuntimeException("Sender must be either MEMBER or COACH");
        }

        // Check quyền chat trong bảng member_coach_selections
        MemberCoachSelection selection = memberCoachSelectionRepository
                .findByMemberAndCoach(member, coach)
                .orElseThrow(() -> new RuntimeException("This member is not allowed to chat with this coach"));

        // Lưu message vào DB
        Message message = new Message();
        message.setSelection(selection);
        message.setSenderType(senderIsMember ? Message.SenderType.MEMBER : Message.SenderType.COACH);
        message.setContent(request.getMessage());
        message.setSentAt(LocalDateTime.now());
        messageRepository.save(message);

        // Broadcast message đến người nhận
        MessageResponse messageResponse = new MessageResponse(
                senderUsername,
                request.getMessage()
        );

        System.out.println("Sending payload to FE: " + messageResponse);

// Broadcast message đến người nhận
        messagingTemplate.convertAndSend(
                "/topic/messages." + request.getReceiverId(),
                messageResponse
        );
    }
}
