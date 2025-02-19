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

    @Value("${file.upload-dir}") // application.properties에서 설정한 경로 사용
    private String uploadDir;

    private static final String UPLOAD_URL_PREFIX = "/uploads/";

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            System.out.println("🚨 업로드된 파일이 비어 있습니다.");
            return null;
        }

        try {
            // ✅ 저장 경로 로그 확인
            System.out.println("📌 파일 저장 디렉토리: " + uploadDir);

            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                boolean created = uploadFolder.mkdirs(); // 디렉토리 생성
                if (created) {
                    System.out.println("✅ 업로드 디렉토리가 생성되었습니다: " + uploadDir);
                } else {
                    System.out.println("🚨 업로드 디렉토리 생성 실패");
                }
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            String filePath = Paths.get(uploadDir, newFilename).toString();
            file.transferTo(new File(filePath));  // ✅ 파일 저장

            String dbFilePath = UPLOAD_URL_PREFIX + newFilename;
            System.out.println("✅ 저장된 파일 경로 (DB): " + dbFilePath);

            return dbFilePath;
        } catch (IOException e) {
            System.out.println("🚨 파일 저장 실패: " + e.getMessage());
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }
    }

    public boolean deleteFile(String imageUrl) {
        try {
            // ✅ 삭제할 파일의 실제 경로 확인
            String filename = imageUrl.replace("/uploads/", "");
            File file = new File(uploadDir + File.separator + filename);

            System.out.println("📌 실제 파일 경로: " + file.getAbsolutePath());

            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("✅ 파일 삭제 성공: " + file.getAbsolutePath());
                    return true;
                } else {
                    System.out.println("🚨 파일 삭제 실패: " + file.getAbsolutePath());
                    return false;
                }
            } else {
                System.out.println("🚨 파일이 존재하지 않음: " + file.getAbsolutePath());
                return false;
            }
        } catch (Exception e) {
            System.out.println("🚨 파일 삭제 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

}
