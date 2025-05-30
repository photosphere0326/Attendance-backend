package com.example.attendance_backend.service;

import com.example.attendance_backend.domain.AttendanceMessage;
import com.example.attendance_backend.domain.Member;
import com.example.attendance_backend.domain.MessageStatus;
import com.example.attendance_backend.dto.AttendanceMessageRequest;
import com.example.attendance_backend.dto.AttendanceMessageResponse;
import com.example.attendance_backend.repository.AttendanceMessageRepository;
import com.example.attendance_backend.repository.MemberRepository;
import com.example.attendance_backend.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceMessageService {

    private final AttendanceMessageRepository attendanceMessageRepository;
    private final MemberRepository memberRepository;
    private final SseEmitterService sseEmitterService;

    @Transactional
    public void sendMessage(Long senderId, AttendanceMessageRequest request) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수신자"));

        AttendanceMessage message = AttendanceMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .attachmentUrl(request.getAttachmentUrl())
                .status(MessageStatus.UNREAD)
                .build();

        attendanceMessageRepository.save(message);

        // ✅ 메시지 ID와 알림 메시지 포함하여 SSE 전송
        String preview = request.getContent().length() > 30
                ? request.getContent().substring(0, 30) + "..."
                : request.getContent();

        String fullMessage = sender.getName() + "님이 메시지를 보냈습니다: " + preview;

        sseEmitterService.sendMessageNotification(receiver.getId(), message.getId(), fullMessage);
    }

    @Transactional(readOnly = true)
    public List<AttendanceMessageResponse> getSentMessages(Long senderId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        return attendanceMessageRepository.findBySenderOrderByCreatedAtDesc(sender).stream()
                .map(AttendanceMessageResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceMessageResponse> getReceivedMessages(Long receiverId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        return attendanceMessageRepository.findByReceiverOrderByCreatedAtDesc(receiver).stream()
                .map(AttendanceMessageResponse::fromEntity)
                .toList();
    }

    @Transactional
    public AttendanceMessageResponse getMessageDetail(Long messageId, Long memberId) {
        AttendanceMessage message = attendanceMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쪽지입니다."));

        if (!message.getSender().getId().equals(memberId) && !message.getReceiver().getId().equals(memberId)) {
            throw new IllegalStateException("이 쪽지를 조회할 권한이 없습니다.");
        }

        if (message.getReceiver().getId().equals(memberId) && message.getStatus() == MessageStatus.UNREAD) {
            message.markAsRead();
        }

        return AttendanceMessageResponse.fromEntity(message);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long memberId) {
        AttendanceMessage message = attendanceMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쪽지입니다."));

        if (!message.getSender().getId().equals(memberId) && !message.getReceiver().getId().equals(memberId)) {
            throw new IllegalStateException("이 쪽지를 삭제할 권한이 없습니다.");
        }

        attendanceMessageRepository.delete(message);
    }
}