package com.example.attendance_backend.repository;

import com.example.attendance_backend.domain.Attendance;
import com.example.attendance_backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByMemberAndDate(Member member, LocalDate date);

    List<Attendance> findByMember(Member member);

    List<Attendance> findByMemberAndDateBetween(Member member, LocalDate start, LocalDate end);
}