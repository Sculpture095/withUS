package com.withus.project.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;  // ✅ 파일 저장 경로 (D:/upload/portfolio/)

    private static final String UPLOAD_URL_PREFIX = "/uploads/";  // ✅ 브라우저에서 접근할 URL prefix

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // ✅ 저장할 디렉토리 생성 (없다면 자동 생성)
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // ✅ 파일 이름을 UUID로 변경하여 저장 (중복 방지)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            // ✅ 파일을 저장할 전체 경로 설정
            String filePath = Paths.get(uploadDir, newFilename).toString();
            file.transferTo(new File(filePath));

            // ✅ DB에 저장할 값은 "/uploads/파일명.png" 형태로 변경
            String dbFilePath = UPLOAD_URL_PREFIX + newFilename;
            System.out.println("✅ DB에 저장될 파일 경로: " + dbFilePath);  // 로그 추가

            return dbFilePath;  // ✅ 이제 "/uploads/파일명.png" 형태로 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }
    }
}
