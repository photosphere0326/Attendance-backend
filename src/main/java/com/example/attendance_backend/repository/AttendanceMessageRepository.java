package com.example.attendance_backend.repository;

import com.example.attendance_backend.domain.AttendanceMessage;
import com.example.attendance_backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceMessageRepository extends JpaRepository<AttendanceMessage, Long> {

    // 받은 쪽지
    List<AttendanceMessage> findByReceiverOrderByCreatedAtDesc(Member receiver);

    // 보낸 쪽지
    List<AttendanceMessage> findBySenderOrderByCreatedAtDesc(Member sender);
}