package com.example.attendance_backend.dto;

import com.example.attendance_backend.domain.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AttendanceResponse {
    private Long attendanceId;
    private Long memberId;
    private String studentName;
    private String time;
    private AttendanceStatus status;
    private LocalDate date;
}