package com.example.attendance_backend.controller;

import com.example.attendance_backend.dto.AttendanceMessageRequest;
import com.example.attendance_backend.dto.AttendanceMessageResponse;
import com.example.attendance_backend.service.AttendanceMessageService;
import com.example.attendance_backend.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class AttendanceMessageController {

    private final AttendanceMessageService attendanceMessageService;
    private final SseEmitterService sseEmitterService;

    // ✅ 쪽지 보내기
    @PostMapping
    public ResponseEntity<Void> sendMessage(
            Authentication authentication,
            @RequestBody AttendanceMessageRequest request
    ) {
        Long senderId = (Long) authentication.getPrincipal();
        attendanceMessageService.sendMessage(senderId, request);
        return ResponseEntity.ok().build();
    }

    // ✅ 내가 받은 쪽지 리스트
    @GetMapping("/received")
    public ResponseEntity<List<AttendanceMessageResponse>> getReceivedMessages(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(attendanceMessageService.getReceivedMessages(memberId));
    }

    // ✅ 내가 보낸 쪽지 리스트
    @GetMapping("/sent")
    public ResponseEntity<List<AttendanceMessageResponse>> getSentMessages(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(attendanceMessageService.getSentMessages(memberId));
    }

    // ✅ 특정 쪽지 상세 보기
    @GetMapping("/{messageId}")
    public ResponseEntity<AttendanceMessageResponse> getMessage(
            @PathVariable Long messageId,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(attendanceMessageService.getMessageDetail(messageId, memberId));
    }

    // ✅ 쪽지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        attendanceMessageService.deleteMessage(messageId, memberId);
        return ResponseEntity.ok().build();
    }

    // ✅ SSE 구독 (실시간 알림 수신용)
    @GetMapping("/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return sseEmitterService.subscribe(memberId);
    }
    // ✅ 쪽지 읽음 처리
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long messageId,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        attendanceMessageService.markAsRead(messageId, memberId);
        return ResponseEntity.ok().build();
    }
}