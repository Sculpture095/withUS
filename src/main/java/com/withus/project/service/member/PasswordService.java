package com.withus.project.service.member;

import com.withus.project.domain.members.MemberEntity;
import com.withus.project.repository.members.MemberRepositoryImpl;
import com.withus.project.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final MemberRepositoryImpl memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    //비밀번호 재설정 링크 생성
    @Transactional
    public String generatePasswordResetLink(String phone){
        MemberEntity member = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 회원을 찾을수 없습니다."));

        //JWT 토큰 생성
        String token = jwtTokenProvider.createToken(member.getId(), "PASSWORD_RESET");
        return "http://localhost:8080/reset-password?token=" + token;
    }

    //비밀번호 변경
    @Transactional
    public void resetPassword(String id, String newPassword){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        //비밀번호 암호화 후 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }
}
