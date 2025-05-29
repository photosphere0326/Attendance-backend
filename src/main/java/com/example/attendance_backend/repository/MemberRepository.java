package com.example.attendance_backend.repository;

import com.example.attendance_backend.domain.Member;
import com.example.attendance_backend.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email); // ✅ 단건 조회
    List<Member> findByRole(Role role);         // ✅ 관리자 목록용
}