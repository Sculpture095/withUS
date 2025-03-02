package com.withus.project.service.member;

import com.withus.project.dto.members.MemberDTO;
import com.withus.project.domain.members.*;
import com.withus.project.mapper.members.MemberMapper;
import com.withus.project.repository.members.MemberRepositoryImpl;
import com.withus.project.util.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepositoryImpl memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EntityManager entityManager;

    /**
     * 회원 등록 (신규 가입)
     *
     * @param dto MemberDTO (요청 데이터)
     * @return 등록된 회원 ID
     */
    @Transactional
    public String registerMember(MemberDTO dto) {
        try {
            // 이메일을 표준화하여 중복 검증에 사용
            String email = dto.getEmail().trim().toLowerCase();
            // 중복 검증 시도
            validateDuplicateMember(dto.getId(), dto.getNickname(), email, dto.getPhone());

            // DTO -> Entity 변환 전에 이메일 값을 갱신
            dto.setEmail(email);
            MemberEntity memberEntity = memberMapper.toEntity(dto);

            if (dto.getRankType() == null || dto.getRankType().isEmpty()) {
                memberEntity.setRankType(RankType.BASIC);
            }

            memberEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
            memberRepository.save(memberEntity);
            handlePcaType(memberEntity, PcaType.valueOf(dto.getPcaType()));
            return memberEntity.getId();
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new IllegalStateException("중복된 키 값이 감지되었습니다. ID, 이메일, 또는 전화번호를 확인하세요.");
            }
            throw new IllegalStateException("데이터베이스 제약 조건 위반이 발생했습니다.", ex);
        }
    }


    /**
     * PCA 유형 처리
     *
     * @param memberEntity 회원 엔티티
     * @param pcaType      PCA 유형
     */
    @Transactional
    public void handlePcaType(MemberEntity memberEntity, PcaType pcaType) {
        if (pcaType == PcaType.PARTNER && memberEntity.getPartner() == null) {
            PartnerEntity partner = new PartnerEntity();
            partner.setMember(memberEntity);
            entityManager.persist(partner);
        } else if (pcaType == PcaType.CLIENT && memberEntity.getClient() == null) {
            ClientEntity client = new ClientEntity();
            client.setMember(memberEntity);
            entityManager.persist(client);
        }
    }


    /**
     * 신규 가입용 중복검증 (문자열 기반)
     *
     * 입력값이 null 또는 빈 문자열이면 검사를 건너뜁니다.
     *
     * @param id       가입할 아이디
     * @param nickname 가입할 닉네임
     * @param email    가입할 이메일
     * @param phone    가입할 전화번호
     */
    public void validateDuplicateMember(String id, String nickname, String email, String phone) {
        if (id != null && !id.trim().isEmpty() && memberRepository.findById(id).isPresent()) {
            throw new IllegalStateException("이미 존재하는 ID입니다: " + id);
        }
        if (nickname != null && !nickname.trim().isEmpty() && memberRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다: " + nickname);
        }
        if (email != null && !email.trim().isEmpty() && memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다: " + email);
        }
        if (phone != null && !phone.trim().isEmpty() && memberRepository.findByPhone(phone).isPresent()) {
            throw new IllegalStateException("이미 존재하는 전화번호입니다: " + phone);
        }
    }

    /**
     * 프로필 수정 시 중복검증 (자신의 정보 제외)
     *
     * @param id        변경할 아이디
     * @param nickname  변경할 닉네임
     * @param email     변경할 이메일
     * @param phone     변경할 전화번호
     * @param currentId 현재 사용자의 ID (본인의 정보는 중복 검사에서 제외)
     */
    public void validateDuplicateMemberExcludingCurrent(String id, String nickname, String email, String phone, String currentId) {
        if (id != null && !id.trim().isEmpty()) {
            Optional<MemberEntity> memberOpt = memberRepository.findById(id);
            if (memberOpt.isPresent() && !memberOpt.get().getId().equals(currentId)) {
                throw new IllegalStateException("이미 존재하는 ID입니다: " + id);
            }
        }
        if (nickname != null && !nickname.trim().isEmpty()) {
            Optional<MemberEntity> memberOpt = memberRepository.findByNickname(nickname);
            if (memberOpt.isPresent() && !memberOpt.get().getId().equals(currentId)) {
                throw new IllegalStateException("이미 존재하는 닉네임입니다: " + nickname);
            }
        }
        if (email != null && !email.trim().isEmpty()) {
            Optional<MemberEntity> memberOpt = memberRepository.findByEmail(email);
            if (memberOpt.isPresent() && !memberOpt.get().getId().equals(currentId)) {
                throw new IllegalStateException("이미 존재하는 이메일입니다: " + email);
            }
        }
        if (phone != null && !phone.trim().isEmpty()) {
            Optional<MemberEntity> memberOpt = memberRepository.findByPhone(phone);
            if (memberOpt.isPresent() && !memberOpt.get().getId().equals(currentId)) {
                throw new IllegalStateException("이미 존재하는 전화번호입니다: " + phone);
            }
        }
    }

    /**
     * 특정 회원 조회
     *
     * @param id 회원 ID
     * @return 회원 DTO
     */
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(String id) {
        MemberEntity memberEntity = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. ID: " + id));
        return memberMapper.toDTO(memberEntity);
    }

    /**
     * 모든 회원 조회
     *
     * @return 모든 회원의 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(memberMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 비밀번호 재설정 링크 생성
     *
     * @param phone 회원 전화번호
     * @return 비밀번호 재설정 링크
     */
    @Transactional
    public String generatePasswordResetLink(String phone) {
        MemberEntity member = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 회원을 찾을 수 없습니다."));
        String token = jwtTokenProvider.createToken(member.getId(), "PASSWORD_RESET");
        return "http://localhost:8080/reset-password?token=" + token;
    }

    /**
     * 비밀번호 변경
     *
     * @param id          회원 ID
     * @param newPassword 새 비밀번호
     */
    @Transactional
    public void resetPassword(String id, String newPassword) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    /**
     * 간편 로그인 (OAuth + JWT 발급)
     *
     * @param email    회원 이메일
     * @param provider OAuth 제공자
     * @return JWT 토큰
     */
    @Transactional
    public String loginWithOAuth(String email, String provider) {
        // 입력받은 이메일을 트림하고 소문자로 변환
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        // 디버깅 로그 추가: 로그인 시도 이메일 출력
        System.out.println("로그인 시도 이메일 (표준화 후): " + email);

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 회원을 찾을 수 없습니다. 입력한 이메일: "));

        // 디버깅 로그: 조회된 회원의 이메일 확인 (예: 저장된 이메일)
        System.out.println("조회된 회원 이메일: " + member.getEmail());

        return jwtTokenProvider.createToken(member.getId(), provider);
    }

    @Transactional
    public String loginWithOAuthById(String id, String provider) {
        if (id != null) {
            id = id.trim();
        }
        System.out.println("로그인 시도 아이디 (표준화 후): " + id);

        // id 필드를 기준으로 회원 조회 (MemberRepositoryImpl의 findById 사용)
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디로 가입된 회원을 찾을 수 없습니다. 입력한 아이디: "));

        System.out.println("조회된 회원 아이디: " + member.getId());
        return jwtTokenProvider.createToken(member.getId(), provider);
    }

    //id로 클라이언트 조회
    public Optional<ClientEntity> findClientById(String id){
        return memberRepository.findClientById(id);
    }

    public MemberEntity findByMemberId(String memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    @Transactional(readOnly = true)
    public MemberDTO getMemberByEmail(String email) {
        final String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        Optional<MemberEntity> optionalMember = memberRepository.findByEmail(normalizedEmail);
        if (optionalMember.isEmpty()) {
            // 로깅: 에러 메시지를 로그에 출력
            System.out.println("해당 이메일로 가입된 회원을 찾을 수 없습니다. 이메일: " + normalizedEmail);
            throw new IllegalArgumentException("해당 이메일로 가입된 회원을 찾을 수 없습니다. 이메일: " + normalizedEmail);
        }
        return memberMapper.toDTO(optionalMember.get());
    }


}
