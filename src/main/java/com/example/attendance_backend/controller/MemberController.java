package com.example.attendance_backend.controller;

import com.example.attendance_backend.domain.Member;
import com.example.attendance_backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/users")
    public List<Member> getAllUsers() {
        return memberRepository.findAll();
    }
}