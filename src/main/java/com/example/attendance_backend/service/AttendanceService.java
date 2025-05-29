package com.example.attendance_backend.service;

import com.example.attendance_backend.domain.*;
import com.example.attendance_backend.dto.AttendanceResponse;
import com.example.attendance_backend.dto.AttendanceUpdateRequest;
import com.example.attendance_backend.repository.AttendanceRepository;
import com.example.attendance_backend.repository.MemberRepository;
import com.example.attendance_backend.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final SseEmitterService sseEmitterService;

    // ✅ 출석 기록
    public void recordAttendance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        attendanceRepository.findByMemberAndDate(member, LocalDate.now())
                .ifPresent(a -> {
                    throw new IllegalStateException("이미 출석했습니다.");
                });

        LocalTime now = LocalTime.now();
        AttendanceStatus status;
        if (now.isBefore(LocalTime.of(8, 0))) {
            throw new IllegalStateException("출석 가능 시간은 08:00부터입니다.");
        } else if (now.isBefore(LocalTime.of(9, 41))) {
            status = AttendanceStatus.ATTENDANCE;
        } else if (now.isBefore(LocalTime.of(14, 1))) {
            status = AttendanceStatus.LATE;
        } else {
            status = AttendanceStatus.ABSENT;
        }

        Attendance attendance = Attendance.builder()
                .member(member)
                .date(LocalDate.now())
                .time(now)
                .status(status)
                .build();

        attendanceRepository.save(attendance);

        AttendanceResponse response = AttendanceResponse.builder()
                .attendanceId(attendance.getId())
                .memberId(member.getId())
                .studentName(member.getName())
                .time(now.toString())
                .status(status)
                .date(LocalDate.now())
                .build();

        // 본인에게 알림
        sseEmitterService.sendAttendanceUpdate(memberId, response);

        // ✅ 관리자에게 출석 알림 메시지 전송
        List<Member> admins = memberRepository.findByRole(Role.ADMIN);
        for (Member admin : admins) {
            sseEmitterService.send(admin.getId(), "[알림] " + member.getName() + "님이 출석했습니다.");
        }
    }

    // ✅ 날짜별 출석 조회
    public AttendanceResponse getStatusByDate(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        Attendance attendance = attendanceRepository.findByMemberAndDate(member, date)
                .orElseThrow(() -> new IllegalStateException("해당 날짜의 출석 기록 없음"));

        return AttendanceResponse.builder()
                .attendanceId(attendance.getId())
                .memberId(member.getId())
                .studentName(member.getName())
                .time(attendance.getTime() != null ? attendance.getTime().toString() : null)
                .status(attendance.getStatus())
                .date(attendance.getDate())
                .build();
    }

    // ✅ 월별 출석 조회
    public List<AttendanceResponse> getMonthlyStatus(Long memberId, int year, int month) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendances = attendanceRepository.findByMemberAndDateBetween(member, start, end);

        return attendances.stream()
                .map(a -> AttendanceResponse.builder()
                        .attendanceId(a.getId())
                        .memberId(member.getId())
                        .studentName(a.getMember().getName())
                        .time(a.getTime() != null ? a.getTime().toString() : null)
                        .status(a.getStatus())
                        .date(a.getDate())
                        .build())
                .toList();
    }

    // ✅ 특정 학생의 모든 출석 내역 조회
    public List<AttendanceResponse> getAllAttendancesForMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        List<Attendance> attendances = attendanceRepository.findByMember(member);

        return attendances.stream()
                .map(a -> AttendanceResponse.builder()
                        .attendanceId(a.getId())
                        .memberId(member.getId())
                        .studentName(a.getMember().getName())
                        .time(a.getTime() != null ? a.getTime().toString() : null)
                        .status(a.getStatus())
                        .date(a.getDate())
                        .build())
                .toList();
    }

    // ✅ 관리자 권한 검증
    private void validateAdmin(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        if (member.getRole() != Role.ADMIN) {
            throw new SecurityException("관리자 권한이 필요합니다.");
        }
    }

    // ✅ 전체 출석 내역 조회 (관리자용)
    public List<AttendanceResponse> getAllAttendanceAsAdmin(Long adminId) {
        validateAdmin(adminId);

        List<Attendance> all = attendanceRepository.findAll();

        return all.stream()
                .map(a -> AttendanceResponse.builder()
                        .attendanceId(a.getId())
                        .memberId(a.getMember().getId())
                        .studentName(a.getMember().getName())
                        .time(a.getTime() != null ? a.getTime().toString() : null)
                        .status(a.getStatus())
                        .date(a.getDate())
                        .build())
                .toList();
    }

    // ✅ 출석 수정
    @Transactional
    public void updateAttendance(Long attendanceId, AttendanceUpdateRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("출석 기록 없음"));

        if (request.getTime() != null) {
            LocalTime newTime = LocalTime.parse(request.getTime());
            attendance.updateTime(newTime);

            AttendanceStatus newStatus;
            if (newTime.isBefore(LocalTime.of(8, 0))) {
                throw new IllegalStateException("출석 가능 시간은 08:00부터입니다.");
            } else if (newTime.isBefore(LocalTime.of(9, 41))) {
                newStatus = AttendanceStatus.ATTENDANCE;
            } else if (newTime.isBefore(LocalTime.of(14, 1))) {
                newStatus = AttendanceStatus.LATE;
            } else {
                newStatus = AttendanceStatus.ABSENT;
            }
            attendance.updateStatus(newStatus);
        }

        if (request.getStatus() != null) {
            attendance.updateStatus(request.getStatus());
        }
    }

    // ✅ 출석 삭제
    @Transactional
    public void deleteAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("출석 기록 없음"));

        attendanceRepository.delete(attendance);
    }
}