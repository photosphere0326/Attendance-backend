package com.example.attendance_backend.controller;

import com.example.attendance_backend.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterService sseEmitterService;

    /**
     * 브라우저용: SSE 연결 (쿼리 파라미터 방식)
     * 예: http://localhost:8080/api/subscribe?memberId=4
     */
    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return sseEmitterService.subscribe(memberId);
    }
}