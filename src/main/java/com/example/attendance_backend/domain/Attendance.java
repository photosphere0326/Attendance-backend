package com.example.attendance_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    // 개별 수정
    public void updateStatus(AttendanceStatus newStatus) {
        this.status = newStatus;
    }

    public void updateTime(LocalTime newTime) {
        this.time = newTime;
    }

    public void updateDate(LocalDate newDate) {
        this.date = newDate;
    }

    // 묶어서 수정 (선택)
    public void updateTimeAndStatus(LocalTime newTime, AttendanceStatus newStatus) {
        this.time = newTime;
        this.status = newStatus;
    }
}