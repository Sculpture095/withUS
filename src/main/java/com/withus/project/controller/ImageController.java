package com.withus.project.controller;

import com.withus.project.service.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
    private final FileUploadService fileUploadService;


    // ✅ 이미지 삭제 API (DELETE 요청)
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(@RequestBody Map<String, String> request) {
        System.out.println("📌 이미지 삭제 요청 받음: " + request);

        String imageUrl = request.get("imageUrl");
        if (imageUrl == null || imageUrl.isEmpty() || imageUrl.contains("@{")) {
            System.out.println("🚨 오류: 유효하지 않은 imageUrl 값: " + imageUrl);
            return ResponseEntity.badRequest().body("잘못된 이미지 URL입니다.");
        }

        System.out.println("📌 삭제할 이미지 URL: " + imageUrl);

        boolean deleted = fileUploadService.deleteFile(imageUrl);
        if (deleted) {
            System.out.println("✅ 이미지 삭제 성공: " + imageUrl);
            return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
        } else {
            System.out.println("🚨 파일이 존재하지 않음 또는 삭제 실패: " + imageUrl);
            return ResponseEntity.ok("이미지가 존재하지 않아 삭제할 필요가 없습니다.");
        }
    }



}
