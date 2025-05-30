package com.example.attendance_backend.repository;

import com.example.attendance_backend.domain.AttendanceMessage;
import com.example.attendance_backend.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceMessageRepository extends JpaRepository<AttendanceMessage, Long> {

    // 받은 쪽지 – sender와 receiver를 미리 로딩
    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<AttendanceMessage> findByReceiverOrderByCreatedAtDesc(Member receiver);

    // 보낸 쪽지 – sender와 receiver를 미리 로딩
    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<AttendanceMessage> findBySenderOrderByCreatedAtDesc(Member sender);
}