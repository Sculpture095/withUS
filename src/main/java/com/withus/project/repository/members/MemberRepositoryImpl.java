package com.withus.project.repository.members;

import com.withus.project.domain.members.ClientEntity;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(transactionManager = "transactionManager")
public class MemberRepositoryImpl extends AbstractRepository<MemberEntity> {

    public MemberRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<MemberEntity> getEntityClass() {
        return MemberEntity.class;
    }

    /**
     * 이메일로 회원 조회
     *
     * @param email 회원 이메일
     * @return 회원 엔터티 (Optional)
     */
    public Optional<MemberEntity> findByEmail(String email) {
        // 디버깅 로그: Repository에 전달된 이메일 값
        System.out.println("findByEmail 호출 - 입력 이메일: " + email);

        Optional<MemberEntity> result = entityManager.createQuery(
                        "SELECT m FROM MemberEntity m WHERE LOWER(m.email) = :email",
                        MemberEntity.class)
                .setParameter("email", email)  // 이미 소문자 변환된 값을 전달
                .getResultStream()
                .findFirst();

        // 디버깅 로그: 조회 결과
        if(result.isPresent()){
            System.out.println("findByEmail 결과: " + result.get().getEmail());
        } else {
            System.out.println("findByEmail 결과: 없음");
        }

        return result;
    }




    /**
     * 전화번호로 회원 조회
     *
     * @param phone 회원 전화번호
     * @return 회원 엔터티 (Optional)
     */
    public Optional<MemberEntity> findByPhone(String phone) {
        return entityManager.createQuery(
                        "SELECT m FROM MemberEntity m WHERE m.phone = :phone",
                        MemberEntity.class)
                .setParameter("phone", phone)
                .getResultStream()
                .findFirst();
    }

    /**
     * 닉네임으로 회원 조회 (중복 확인용)
     *
     * @param nickname 회원 닉네임
     * @return 회원 엔터티 (Optional)
     */
    public Optional<MemberEntity> findByNickname(String nickname) {
        return entityManager.createQuery(
                        "SELECT m FROM MemberEntity m WHERE m.nickname = :nickname",
                        MemberEntity.class)
                .setParameter("nickname", nickname)
                .getResultStream()
                .findFirst();
    }



    /**
     * 사용자 ID를 통해 ClientEntity 조회
     *
     * @param id 사용자 ID
     * @return 클라이언트 엔터티 (Optional)
     */
    public Optional<ClientEntity> findClientById(String id) {
        return entityManager.createQuery(
                        "SELECT c FROM ClientEntity c " +
                                "WHERE c.member.memberIdx = (SELECT m.memberIdx FROM MemberEntity m WHERE m.id = :id)",
                        ClientEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
    public Optional<PartnerEntity> findPartnerById(String id){
        return entityManager.createQuery(
                "SELECT p FROM PartnerEntity  p " +
                        "WHERE p.member.memberIdx = (SELECT m.memberIdx FROM MemberEntity m WHERE m.id = :id)",
                PartnerEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();

    }

    public Integer findPartnerIdxByMemberId(String memberId) {
        try {
            return entityManager.createQuery(
                            "SELECT p.partnerIdx FROM PartnerEntity p WHERE p.member.id = :memberId", Integer.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // partnerIdx가 없는 경우 null 반환
        }
    }

    /**
     * 사용자 ID를 통해 Client 또는 Partner 조회
     *
     * @param userId 사용자 ID
     * @return ClientEntity 또는 PartnerEntity (Optional)
     */
    public Optional<Object> findByUserId(String userId) {
        Optional<ClientEntity> client = findClientById(userId);
        if (client.isPresent()) {
            return Optional.of(client.get());
        }

        Optional<PartnerEntity> partner = findPartnerById(userId);
        return partner.isPresent() ? Optional.of(partner.get()) : Optional.empty();
    }

    /**
     * 사용자 ID로 회원 조회
     *
     * @param id 회원 ID
     * @return 회원 엔티티 (Optional)
     */
    public Optional<MemberEntity> findById(String id) {
        return entityManager.createQuery(
                        "SELECT m FROM MemberEntity m WHERE m.id = :id",
                        MemberEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }




    /**
     * 사용자 ID를 통해 Member + Client 혹은 Member + Partner 삭제 불가
     *
     * 이유: 회원은 Client 또는 Partner와 반드시 쌍으로 존재해야 하므로 삭제 제한.
     * 삭제하려면 명시적으로 Member와 관련된 Client/Partner 삭제를 수행해야 함.
     */
    public void deleteMemberWithAssociations(String id) {
        throw new UnsupportedOperationException("Member와 Client/Partner는 세트로만 삭제 가능합니다.");
    }

    @Transactional
    public void deleteAll() {
        entityManager.createQuery("DELETE FROM ClientEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM PartnerEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM MemberEntity").executeUpdate();
    }
}
