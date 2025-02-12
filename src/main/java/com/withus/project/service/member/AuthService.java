package com.withus.project.service.member;

import com.withus.project.domain.members.MemberEntity;
import com.withus.project.repository.members.MemberRepositoryImpl;
import com.withus.project.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepositoryImpl memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    //간편 로그인(OAuth + JWT 발급)

    @Transactional
    public String loginWithOAuth(String email, String provider){
        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 회원을 찾을수 없습니다."));

        //JWT 토큰 생성
        return jwtTokenProvider.createToken(member.getId(), provider);
    }
}
