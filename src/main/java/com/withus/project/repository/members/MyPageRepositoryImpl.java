package com.withus.project.repository.members;

import com.withus.project.domain.members.MemberEntity;
import com.withus.project.domain.members.MyPageEntity;
import com.withus.project.repository.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public class MyPageRepositoryImpl extends AbstractRepository<MyPageEntity> {

    public MyPageRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<MyPageEntity> getEntityClass() {
        return MyPageEntity.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return super.getEntityManager();
    }

    /**
     * 특정 idx를 기반으로 MyPageEntity 전체 업데이트
     *
     * @param id 업데이트할 엔터티의 식별자
     * @param updatedEntity 업데이트된 MyPageEntity
     */
    public void update(String id, MyPageEntity updatedEntity) {
        super.update(id, updatedEntity);
    }

    /**
     * 특정 idx를 기반으로 MyPageEntity 일부 필드 업데이트
     *
     * @param id 업데이트할 엔터티의 식별자
     * @param fields 업데이트할 필드와 값의 맵
     */
    public void partialUpdate(UUID myPageId, Map<String, Object> fields) {
        Integer myPageIdx = findIdxByMyPageId(myPageId)
                .orElseThrow(() -> new EntityNotFoundException("해당 myPageId에 대한 MyPage 정보를 찾을 수 없습니다: " + myPageId));

        super.partialUpdate(myPageIdx, fields); // ✅ 변환 후 업데이트 실행
    }


    /**
     * Member ID를 기반으로 MyPageEntity 단일 조회
     *
     * @param id 사용자 ID
     * @return MyPageEntity (Optional)
     */
    public Optional<MyPageEntity> findSingleByMemberId(String id) {
        return super.findAllByMemberId(id).stream().findFirst();
    }


    public UUID findMyPageIdByMemberId(String memberId) {
        try {
            return getEntityManager().createQuery(
                            "SELECT m.myPageId FROM MyPageEntity m WHERE m.member.id = :memberId", UUID.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // 해당 사용자의 마이페이지가 없을 경우 null 반환
        }
    }

    public Optional<MyPageEntity> findByMyPageId(UUID myPageId) {
        return getEntityManager().createQuery(
                        "SELECT m FROM MyPageEntity m WHERE m.myPageId = :myPageId", MyPageEntity.class)
                .setParameter("myPageId", myPageId)
                .getResultStream()
                .findFirst();
    }

    public Optional<Integer> findIdxByMyPageId(UUID myPageId) {
        return getEntityManager().createQuery(
                        "SELECT m.myPageIdx FROM MyPageEntity m WHERE m.myPageId = :myPageId", Integer.class)
                .setParameter("myPageId", myPageId)
                .getResultStream()
                .findFirst();
    }

    @Transactional
    public void partialUpdateByMyPageId(UUID myPageId, Map<String, Object> fields) {
        Optional<Integer> idxOptional = findIdxByMyPageId(myPageId);

        if (idxOptional.isEmpty()) {
            throw new EntityNotFoundException("해당 myPageId에 대한 myPageIdx를 찾을 수 없습니다: " + myPageId);
        }

        Integer myPageIdx = idxOptional.get();

        // 동적으로 update query 생성
        StringBuilder queryBuilder = new StringBuilder("UPDATE MyPageEntity m SET ");
        for (String key : fields.keySet()) {
            queryBuilder.append("m.").append(key).append(" = :").append(key).append(", ");
        }
        queryBuilder.setLength(queryBuilder.length() - 2); // 마지막 콤마 제거
        queryBuilder.append(" WHERE m.myPageIdx = :myPageIdx");

        var query = getEntityManager().createQuery(queryBuilder.toString());
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        query.setParameter("myPageIdx", myPageIdx);
        query.executeUpdate();
    }







    /**
     * UUID 기반으로 MyPageEntity 단일 조회
     *
     * @param myPageId MyPage UUID
     * @return MyPageEntity (Optional)
     */
    public Optional<MyPageEntity> findSingleByMyPageId(UUID myPageId) {
        return getEntityManager().createQuery(
                        "SELECT m FROM MyPageEntity m WHERE m.myPageId = :myPageId", MyPageEntity.class)
                .setParameter("myPageId", myPageId)
                .getResultStream()
                .findFirst();
    }


    public void createDefaultMyPage(MemberEntity member) {
        MyPageEntity newMyPage = new MyPageEntity();
        newMyPage.setMember(member);
        newMyPage.setIntroduce("자기소개를 입력하세요.");
        newMyPage.setBirth(null);  // 최초 등록 시 생년월일 없음
        newMyPage.setBusinessNum(0);  // 사업자번호 기본값 0
        newMyPage.setAddress("미설정");  // 기본 주소
        newMyPage.setZipcode("00000");  // 기본 우편번호
        newMyPage.setProfile(null);  // 프로필 사진 없음
        newMyPage.setAccount(null);  // 계좌번호 없음

        // ✅ `mypage_id`는 `@PrePersist`에서 자동 생성됨 (UUID)
        getEntityManager().persist(newMyPage);
        System.out.println("✅ [MyPageRepository] 기본 마이페이지 생성 완료: " + member.getId());
    }


}

//수정완