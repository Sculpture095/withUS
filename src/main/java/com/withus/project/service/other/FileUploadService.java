package com.withus.project.service.other;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${cloud.ncloud.object-storage.endpoint}")
    private String ncloudEndpoint;

    @Value("${cloud.ncloud.object-storage.region}")
    private String ncloudRegion; // "kr-standard" 등

    @Value("${cloud.ncloud.object-storage.access-key}")
    private String accessKey;

    @Value("${cloud.ncloud.object-storage.secret-key}")
    private String secretKey;

    @Value("${cloud.ncloud.object-storage.bucket}")
    private String bucketName;

    private S3Client s3Client;

    /**
     * 서비스 초기화 시점에 S3Client 생성
     */
    @PostConstruct
    private void init() {
        // endpointOverride 로 NCP Object Storage 주소 설정
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(ncloudEndpoint))
                // Region 설정 (AWS SDK에서는 필수)
                .region(Region.AP_NORTHEAST_2)
                // credentials 설정
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    /**
     * 파일 업로드 (NCP Object Storage)
     * @param file MultipartFile
     * @return 업로드 후 접근 가능한 URL
     */
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            System.out.println("🚨 업로드된 파일이 비어 있습니다.");
            return null;
        }

        try {
            // 원본 파일명
            String originalFilename = file.getOriginalFilename();
            // 확장자 추출
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // 새 파일명 생성
            String newFilename = UUID.randomUUID().toString() + extension;

            // NCP S3에 저장할 경로(예: "portfolios/xxxx-xxxx.jpg")
            String s3Key = "Files/" + newFilename;

            // MultipartFile → 임시 파일로 변환
            File tempFile = File.createTempFile("temp-", extension);
            file.transferTo(tempFile);

            // putObject: Naver Object Storage(S3 호환)에 업로드
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(file.getContentType())
                            // 공개로 하고 싶다면 (퍼블릭 ACL)
                            .acl("public-read")
                            .build(),
                    RequestBody.fromFile(tempFile)
            );

            // 임시 파일 삭제
            if (tempFile.exists()) {
                tempFile.delete();
            }

            // 네이버 오브젝트 스토리지는
            // "https://kr.object.ncloudstorage.com/버킷명/오브젝트키" 로 접근
            // endpoint + "/" + bucketName + "/" + s3Key
            String fileUrl = ncloudEndpoint + "/" + bucketName + "/" + s3Key;

            System.out.println("✅ 업로드 완료: " + fileUrl);
            return fileUrl;

        } catch (IOException e) {
            System.out.println("🚨 파일 변환 실패: " + e.getMessage());
            throw new RuntimeException("파일 변환 실패", e);
        } catch (S3Exception e) {
            System.out.println("🚨 Naver Object Storage 업로드 실패: " + e.getMessage());
            throw new RuntimeException("Naver Object Storage 업로드 실패", e);
        }
    }

    /**
     * 파일 삭제
     * @param imageUrl storeFile()로부터 반환된 파일의 URL
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(String imageUrl) {
        try {
            // imageUrl 예시:
            // https://kr.object.ncloudstorage.com/my-ncloud-bucket/portfolios/xxx.jpg
            // → 실제로 삭제할 오브젝트 키(s3Key)는 "portfolios/xxx.jpg"
            String baseUrl = ncloudEndpoint + "/" + bucketName + "/";
            // baseUrl 제거 후 남는 부분이 s3Key
            String s3Key = imageUrl.replace(baseUrl, "");

            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .build()
            );
            System.out.println("✅ 삭제 성공: " + s3Key);
            return true;

        } catch (S3Exception e) {
            System.out.println("🚨 삭제 실패: " + e.getMessage());
            return false;
        }
    }

}
