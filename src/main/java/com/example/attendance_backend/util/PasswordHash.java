package com.example.attendance_backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHash {

    public static void main(String[] args) {
        // 원본 비밀번호
        String rawPassword = "qwe123";

        // BCrypt 인코더 생성
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 암호화
        String encodedPassword = encoder.encode(rawPassword);

        // 결과 출력
        System.out.println("Encoded password for '" + rawPassword + "': " + encodedPassword);
    }
}