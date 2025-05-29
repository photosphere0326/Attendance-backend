package com.example.attendance_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceMessageRequest {
    private Long receiverId;        // 관리자 ID (혹은 학생 ID)
    private String content;         // 쪽지 내용
    private String attachmentUrl;   // 파일/이미지 URL (선택)
}