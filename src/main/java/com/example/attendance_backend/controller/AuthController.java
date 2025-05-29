package com.example.attendance_backend.controller;

import com.example.attendance_backend.domain.Member;
import com.example.attendance_backend.domain.Role;
import com.example.attendance_backend.dto.LoginRequest;
import com.example.attendance_backend.dto.LoginResponse;
import com.example.attendance_backend.dto.SignupRequest;
import com.example.attendance_backend.dto.CommonResponse;
import com.example.attendance_backend.repository.MemberRepository;
import com.example.attendance_backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody SignupRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new CommonResponse("이미 사용 중인 이메일입니다.", false));
        }

        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .build();

        memberRepository.save(member);
        return ResponseEntity.ok(new CommonResponse("회원가입 성공", true));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return memberRepository.findByEmail(request.getEmail())
                .filter(member -> passwordEncoder.matches(request.getPassword(), member.getPassword()))
                .map(member -> {
                    String token = jwtTokenProvider.generateToken(
                            member.getId(),
                            member.getRole().name(),
                            member.getName()
                    );
                    return ResponseEntity.ok(new LoginResponse(token));
                })
                .orElseGet(() -> ResponseEntity
                        .badRequest()
                        .body(new LoginResponse("")));
    }
}