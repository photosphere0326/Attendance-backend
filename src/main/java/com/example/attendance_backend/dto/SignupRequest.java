package com.example.attendance_backend.dto;

import com.example.attendance_backend.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private Role role; // 보통은 STUDENT만 받도록 처리 (관리자 직접 등록)
}