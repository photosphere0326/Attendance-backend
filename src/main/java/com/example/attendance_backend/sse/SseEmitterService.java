package com.example.attendance_backend.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.example.attendance_backend.dto.AttendanceResponse;
@Service
public class SseEmitterService {

    // 사용자 ID 기준으로 SseEmitter를 저장할 맵
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        // 연결 직후 더미 이벤트 전송 (클라이언트 연결 테스트용)
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 완료"));
        } catch (IOException e) {
            emitters.remove(memberId);
        }

        return emitter;
    }

    public void send(Long memberId, String message) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(message));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }

    // ✅ 쪽지 수신 알림 전용 메서드
    public void sendMessageNotification(Long receiverId) {
        SseEmitter emitter = emitters.get(receiverId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message-received")
                        .data("새 쪽지가 도착했습니다."));
            } catch (IOException e) {
                emitters.remove(receiverId);
            }
        }
    }
    // ✅ 출석 수신 알림 전용 메서드

    public void sendAttendanceUpdate(Long memberId, AttendanceResponse response) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("attendance")
                        .data(response));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }
}