package com.example.attendance_backend.controller;

import com.example.attendance_backend.dto.AttendanceResponse;
import com.example.attendance_backend.dto.AttendanceUpdateRequest;
import com.example.attendance_backend.dto.MemberSimpleDto;
import com.example.attendance_backend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 🔐 관리자 API

    // 전체 출석 내역 조회
    @GetMapping("/admin/all")
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance(Authentication authentication) {
        Long adminId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(attendanceService.getAllAttendanceAsAdmin(adminId));
    }

    // 특정 학생 전체 출석 내역
    @GetMapping("/admin/member/{memberId}")
    public ResponseEntity<List<AttendanceResponse>> getAllForMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(attendanceService.getAllAttendancesForMember(memberId));
    }

    // 학생 목록 (id, 이름만)
    @GetMapping("/admin/students")
    public ResponseEntity<List<MemberSimpleDto>> getAllStudents() {
        return ResponseEntity.ok(attendanceService.getAllStudents());
    }

    // 출석 수정
    @PutMapping("/admin/{attendanceId}")
    public ResponseEntity<Void> updateAttendance(@PathVariable Long attendanceId,
                                                 @RequestBody AttendanceUpdateRequest request) {
        attendanceService.updateAttendance(attendanceId, request);
        return ResponseEntity.ok().build();
    }

    // 출석 삭제
    @DeleteMapping("/admin/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.ok().build();
    }

    // 👤 학생 API

    // 출석 기록
    @PostMapping
    public ResponseEntity<Void> attend(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        attendanceService.recordAttendance(memberId);
        return ResponseEntity.ok().build();
    }

    // 오늘 출석
    @GetMapping("/today")
    public ResponseEntity<AttendanceResponse> getToday(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(attendanceService.getStatusByDate(memberId, LocalDate.now()));
    }

    // 일별 조회
    @GetMapping("/daily")
    public ResponseEntity<AttendanceResponse> getDailyStatus(Authentication authentication,
                                                             @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(attendanceService.getStatusByDate(memberId, date));
    }

    // 월별 조회
    @GetMapping("/monthly")
    public ResponseEntity<List<AttendanceResponse>> getMonthlyStatus(Authentication authentication,
                                                                     @RequestParam("year") int year, @RequestParam("month") int month) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(attendanceService.getMonthlyStatus(memberId, year, month));
    }
    // ✅ 관리자: 특정 학생의 월별 출석 조회
    @GetMapping("/admin/member/{memberId}/monthly")
    public ResponseEntity<List<AttendanceResponse>> getMonthlyForMemberAsAdmin(
            @PathVariable Long memberId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        return ResponseEntity.ok(attendanceService.getMonthlyStatus(memberId, year, month));
    }
}