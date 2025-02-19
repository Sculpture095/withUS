package com.withus.project.service;

import com.withus.project.config.CustomUserDetails;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.repository.members.MemberRepositoryImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepositoryImpl memberRepository;

    public CustomUserDetailsService(MemberRepositoryImpl memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        // ✅ DB에서 사용자 정보 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberId));

        // ✅ 권한 설정 (필요하면 ROLE_USER 등 추가 가능)
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return CustomUserDetails.builder()
                .username(member.getId())  // member의 ID를 username으로 설정
                .password(member.getPassword()) // member의 비밀번호 설정
                .authorities(authorities) // 권한 추가
                .nickname(member.getNickname()) // 닉네임 추가
                .build();
    }
}
