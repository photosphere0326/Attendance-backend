package com.example.attendance_backend.dto;

import com.example.attendance_backend.domain.AttendanceMessage;
import com.example.attendance_backend.domain.MessageStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String content;
    private String receiverName;
    private String attachmentUrl;
    private String createdAt; // ISO-8601 문자열
    private MessageStatus status; // ✅ 메시지 상태 추가됨

    public static AttendanceMessageResponse fromEntity(AttendanceMessage msg) {
        return AttendanceMessageResponse.builder()
                .id(msg.getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getName())
                .receiverId(msg.getReceiver().getId())
                .receiverName(msg.getReceiver().getName())
                .content(msg.getContent())
                .attachmentUrl(msg.getAttachmentUrl())
                .createdAt(msg.getCreatedAt().toString())
                .status(msg.getStatus()) // ✅ 상태 포함
                .build();
    }
}