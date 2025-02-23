package com.withus.project.controller;

import com.withus.project.config.CustomUserDetails;
import com.withus.project.domain.boards.RemarkEntity;
import com.withus.project.service.RemarkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/remarks")
public class RemarkController {

    private final RemarkService remarkService;
    private final StringRedisTemplate redisTemplate;

    //특정 게시글의 댓글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<?> getRemarks(@PathVariable String boardId) {
        System.out.println("📌 댓글 불러오기 요청 - boardId: " + boardId);

        List<RemarkEntity> remarks = remarkService.getRemarksByBoardId(boardId);
        if (remarks.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // ✅ JSON 응답 데이터 변환
        List<Map<String, Object>> response = remarks.stream().map(remark -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", remark.getRemarkId());
            map.put("content", remark.getRemarks());
            map.put("author", remark.getMember().getNickname());
            map.put("createdDate", remark.getCreateDate());
            map.put("likeCount", remark.getLikeCount());
            map.put("memberId",remark.getMember().getId());
            map.put("parentId",
                    remark.getParentRemark() != null
                            ? remark.getParentRemark().getRemarkId()  // 여기도 remarkId(문자열)로
                            : null
            );
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ✅ 댓글 작성 (대댓글 포함)
    @PostMapping("/write")
    public ResponseEntity<String> writeRemark(
            @RequestParam String boardId,
            @RequestParam String content,
            @RequestParam(required = false) String parentRemarkId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String memberId = userDetails.getUsername();  // == 실제로는 member의 "id" (예: user01)
        System.out.println("현재 로그인 사용자: " + memberId);

        if ("null".equals(parentRemarkId)) {
            parentRemarkId = null;
        }

        System.out.println("📌 댓글 작성 요청 - boardId: " + boardId + ", content: " + content + ", parentId: " + parentRemarkId);

        remarkService.saveRemark(boardId, memberId, content, parentRemarkId);
        return ResponseEntity.ok("댓글이 작성되었습니다.");
    }


    // ✅ 댓글 수정
    @PutMapping("/edit/{remarkId}")
    public ResponseEntity<String> editRemark(
            @PathVariable String remarkId,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        remarkService.updateRemark(remarkId, userDetails.getUsername(), content);
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }


    // ✅ 댓글 삭제
    @DeleteMapping("/delete/{remarkId}")
    public ResponseEntity<String> deleteRemark(
            @PathVariable String remarkId,
            @AuthenticationPrincipal String memberId) {
        remarkService.deleteRemark(remarkId, memberId);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    //특정 부모 댓글의모든 대댓글 조회
    @GetMapping("/replies/{parentId}")
    public ResponseEntity<List<RemarkEntity>> getReplies(@PathVariable String parentId){
        return ResponseEntity.ok(remarkService.findChildRemarks(parentId));
    }

    // ✅ 댓글 좋아요 증가
    @PostMapping("/like/{remarkId}")
    public ResponseEntity<String> likeComment(@PathVariable String remarkId, HttpServletRequest request, Principal principal) {
        // 로그인 체크: principal이 null이면 로그인되지 않은 상태
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String userIdentifier = getUserIdentifier(request);
        String redisKey = "likedComment:" + remarkId + ":" + userIdentifier;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        if (redisTemplate.hasKey(redisKey)) {
            return ResponseEntity.badRequest().body("이미 추천한 댓글입니다.");
        }

        remarkService.increaseLikeCount(remarkId);
        ops.set(redisKey, "liked", 365, TimeUnit.DAYS);

        return ResponseEntity.ok("댓글 추천이 반영되었습니다.");
    }

    private String getUserIdentifier(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        }
        return request.getRemoteAddr(); // 로그인하지 않은 경우 IP 기반으로 처리
    }




}
