package com.example.attendance_backend.sse;

import com.example.attendance_backend.dto.AttendanceResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));
        emitter.onError((e) -> emitters.remove(memberId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공"));
        } catch (IOException e) {
            emitters.remove(memberId);
        }

        return emitter;
    }

    // ✅ 쪽지 도착 알림 전용 (이벤트 이름: "message-received") - JSON 데이터 전송
    public void sendMessageNotification(Long receiverId, Long messageId, String content) {
        SseEmitter emitter = emitters.get(receiverId);
        if (emitter != null) {
            try {
                // 프론트에서 JSON.parse() 하기 좋게 포맷
                String payload = String.format("{\"id\":%d,\"title\":\"%s\",\"message\":\"%s\"}",
                        messageId,
                        "쪽지 도착",
                        (content.length() > 30 ? content.substring(0, 30) + "..." : content).replace("\"", "\\\"")
                );

                emitter.send(SseEmitter.event()
                        .name("message-received")
                        .data(payload));
            } catch (IOException e) {
                emitters.remove(receiverId);
            }
        }
    }

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