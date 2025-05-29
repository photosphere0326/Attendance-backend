package com.example.attendance_backend.dto;

import com.example.attendance_backend.domain.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceUpdateRequest {
    private String time; // "HH:mm" 형식
    private AttendanceStatus status;
}