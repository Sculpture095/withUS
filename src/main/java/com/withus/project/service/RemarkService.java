package com.withus.project.service;

import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.boards.RemarkEntity;
import com.withus.project.domain.dto.boards.RemarkDTO;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.mapper.boards.RemarkMapper;
import com.withus.project.repository.boards.RemarkRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemarkService {

    private final RemarkRepositoryImpl remarkRepository;
    private final RemarkMapper remarkMapper;



    /**
     * 댓글 등록
     * @param dto RemarkDTO
     * @return 등록된 댓글의 DTO
     */
    @Transactional
    public RemarkDTO addRemark(RemarkDTO dto) {
        RemarkEntity entity = remarkMapper.toEntity(dto);

        if (dto.getRemarkIdx() != null) {
            RemarkEntity parentRemark = remarkRepository.findByRemarkId(dto.getRemarkId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent remark not found."));
            entity.setParentRemark(parentRemark);
            entity.setDepth(parentRemark.getDepth() + 1);
        }

        entity = remarkRepository.save(entity);
        return remarkMapper.toDTO(entity);
    }

    /**
     * 댓글 수정
     * @param remarkId 수정할 댓글의 식별자
     * @param memberId 작성자 식별자
     * @param newRemarks 새로운 댓글 내용
     */
    @Transactional
    public void updateRemark(String remarkId, String memberId, String newRemarks) {
        RemarkEntity remark = remarkRepository.findByRemarkId(remarkId)
                .orElseThrow(() -> new IllegalArgumentException("Remark not found."));

        if (!remark.getMember().getMemberIdx().equals(memberId)) {
            throw new SecurityException("Only the author can update the remark.");
        }

        remark.setRemarks(newRemarks);
        remarkRepository.save(remark);
    }

    /**
     * 댓글 삭제
     * @param remarkId 삭제할 댓글의 식별자
     * @param memberId 작성자 식별자
     */
    @Transactional
    public void deleteRemark(String remarkId, String memberId) {
        RemarkEntity remark = remarkRepository.findByRemarkId(remarkId)
                .orElseThrow(() -> new IllegalArgumentException("Remark not found."));

        if (!remark.getMember().getMemberIdx().equals(memberId)) {
            throw new SecurityException("Only the author can delete the remark.");
        }

        remarkRepository.delete(remark);
    }
    /**
     * 특정 게시판의 모든 댓글 조회 (계층 구조)
     * @param boardId 게시판 식별자
     * @return 계층 구조로 정렬된 댓글 리스트
     */
    @Transactional
    public List<RemarkDTO> getRemarksByBoard(String boardId) {
        List<RemarkEntity> remarks = remarkRepository.findAllByBoardId(boardId);
        return remarks.stream()
                .map(remarkMapper::toDTO)
                .collect(Collectors.toList());
    }
}
