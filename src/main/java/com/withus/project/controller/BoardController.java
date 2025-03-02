package com.withus.project.controller;

import com.withus.project.config.CustomUserDetails;
import com.withus.project.domain.boards.BoardEntity;
import com.withus.project.domain.boards.BoardType;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.service.BoardService;
import com.withus.project.service.other.FileUploadService;
import com.withus.project.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;
    private final FileUploadService fileUploadService;
    private final StringRedisTemplate redisTemplate;


    // ✅ 게시판 이동 (BoardType에 맞게 페이지 이동)
    @GetMapping("/{boardType}")
    public String viewBoard(@PathVariable String boardType,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String keyword,
                            Model model) {

        BoardType type;
        try {
            type = BoardType.valueOf(boardType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "redirect:/"; // 잘못된 URL이면 홈으로 리디렉션
        }

        // ✅ 디버깅용 로그 추가
        System.out.println("📌 BoardType: " + type);
        System.out.println("📌 검색어: " + keyword);

        Page<BoardEntity> boardPage = boardService.getBoardsByType(type, keyword, PageRequest.of(page, size, Sort.by("createDate").descending()));

        // ✅ 데이터가 제대로 들어오는지 확인
        for (BoardEntity board : boardPage.getContent()) {
            System.out.println("📌 게시글 제목: " + board.getSubject());
            System.out.println("📌 작성자: " + (board.getMember() != null ? board.getMember().getNickname() : "❌ 멤버 없음"));
        }

        model.addAttribute("boardPage", boardPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardType", type);

        switch (type) {
            case FREE_BOARD:
                return "board/free_board";
            case IT_BOARD:
                return "board/it_board";
            case NOTICE_BOARD:
                return "board/notice_board";
            default:
                return "redirect:/";
        }
    }

    // ✅ 게시글 상세 조회 (조회수 중복 방지 추가)
    @GetMapping("/detail/{boardId}")
    public String viewBoardDetail(@PathVariable String boardId, Model model, HttpServletRequest request, HttpServletResponse response) {
        BoardEntity board = boardService.getBoardByBoardId(boardId);
        if (board == null) {
            return "redirect:/board/it_board"; // 게시글이 없으면 게시판으로 리디렉션
        }

        // ✅ 사용자 식별 (로그인 사용자는 ID, 비로그인은 IP 사용)
        String userIdentifier = getUserIdentifier(request);

        // ✅ Redis에 "게시물 조회 기록" 저장 (24시간 유지)
        String redisKey = "viewedPost:" + boardId + ":" + userIdentifier;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        if (!redisTemplate.hasKey(redisKey)) {
            // ✅ Redis에 기록이 없으면 조회수 증가
            boardService.increaseViewCount(boardId);
            ops.set(redisKey, "viewed", 24, TimeUnit.HOURS);
        }

        model.addAttribute("board", board);
        return "board/post-view"; // ✅ 게시글 상세보기 페이지
    }

    private String getUserIdentifier(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        }
        return request.getRemoteAddr(); // 로그인하지 않은 경우 IP 기반으로 처리
    }

    @PostMapping("/like/{boardId}")
    public ResponseEntity<String> likePost(@PathVariable String boardId, HttpServletRequest request, Principal principal) {
        // 로그인 체크: principal이 null이면 로그인되지 않은 상태
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String userIdentifier = getUserIdentifier(request);
        String redisKey = "likedPost:" + boardId + ":" + userIdentifier;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        if (redisTemplate.hasKey(redisKey)) {
            return ResponseEntity.badRequest().body("이미 추천한 게시물입니다.");
        }

        boardService.increaseLikeCount(boardId);
        ops.set(redisKey, "liked", 365, TimeUnit.DAYS);

        return ResponseEntity.ok("추천이 반영되었습니다.");
    }

    @GetMapping("/write-post")
    public String showWritePostPage() {
        return "board/write-post"; // templates/board/write-post.html 로 이동
    }


    @PostMapping("/write")
    public String writePost(@RequestParam("boardType") String boardType,
                            @RequestParam("subject") String subject,
                            @RequestParam("content") String content,
                            @RequestParam(value = "file", required = false) MultipartFile file,
                            Principal principal) {

        // 로그인 체크
        if (principal == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/login?error=로그인이 필요합니다.";
        }

        System.out.println("📌 게시글 작성 요청 받음");
        System.out.println("📌 제목: " + subject);
        System.out.println("📌 내용: " + content);
        System.out.println("📌 게시판 타입: " + boardType);

        if (file != null) {
            System.out.println("📌 업로드된 파일 이름: " + file.getOriginalFilename());
        } else {
            System.out.println("📌 업로드된 파일이 없음");
        }

        String username = principal.getName();
        MemberEntity member = memberService.findByMemberId(username);
        if (member == null) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다: " + username);
        }

        BoardEntity board = new BoardEntity();
        board.setSubject(subject);
        board.setContent(content);
        board.setBoardType(BoardType.valueOf(boardType));
        board.setMember(member);

        boardService.saveBoard(board, file);

        System.out.println("✅ 게시글이 성공적으로 저장되었습니다.");

        return "redirect:/board/" + boardType.toLowerCase();
    }




    // 게시글 수정 페이지 이동
    @GetMapping("/edit/{boardId}")
    public String editBoard(@PathVariable String boardId, Model model, Principal principal) {
        BoardEntity board = boardService.getBoardByBoardId(boardId);

        if (board == null) {
            return "redirect:/board"; // 게시글이 없으면 게시판 목록으로 이동
        }

        if (!board.getMember().getId().equals(principal.getName())) {
            return "redirect:/board/" + board.getBoardType().name().toLowerCase();
        }

        model.addAttribute("board", board);
        return "board/edit-post";
    }



    // 게시글 수정 요청 처리
    @PostMapping("/edit/{boardId}")
    public String updateBoard(@PathVariable String boardId,
                              @RequestParam String subject,
                              @RequestParam String content,
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              @RequestParam(value = "deleteImage", required = false, defaultValue = "false") boolean deleteImage,
                              Principal principal) {

        String memberId = principal.getName();

        // ✅ 이미지 삭제 요청이 있을 경우 file을 null로 설정
        if (deleteImage) {
            file = null;
        }

        boardService.updateBoard(boardId, subject, content, file, memberId);

        return "redirect:/board/detail/" + boardId;
    }

    @PostMapping("/delete/{boardId}")
    public String deleteBoard(@PathVariable String boardId, Principal principal) {
        String memberId = principal.getName();
        BoardEntity board = boardService.getBoardByBoardId(boardId);

        if (board == null) {
            return "redirect:/"; // ✅ 게시글이 없으면 홈으로 이동
        }

        // ✅ 로그인한 사용자가 작성자인지 확인
        if (!board.getMember().getId().equals(memberId)) {
            return "redirect:/board/detail/" + boardId; // ✅ 작성자가 아니라면 게시글 상세 페이지로 이동
        }

        // ✅ 게시판 타입 가져오기 (FREE_BOARD, IT_BOARD 등)
        String boardType = board.getBoardType().name().toLowerCase();

        boardService.deleteBoard(boardId);

        // ✅ 삭제 후 해당 게시판 목록으로 이동
        return "redirect:/board/" + boardType;
    }






    @GetMapping("/debug-auth")
    public ResponseEntity<String> debugAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ResponseEntity.ok("현재 로그인된 사용자: " + ((CustomUserDetails) principal).getUsername());
        } else {
            return ResponseEntity.ok("현재 로그인된 사용자 정보: " + principal.toString());
        }
    }


}
