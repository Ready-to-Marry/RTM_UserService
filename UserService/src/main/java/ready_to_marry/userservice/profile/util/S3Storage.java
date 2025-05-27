package ready_to_marry.userservice.profile.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.userservice.profile.config.S3Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * S3 파일 저장 및 삭제 기능을 제공하는 유틸리티 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class S3Storage {
    private final S3Properties s3Properties;
    private S3Client s3Client;

    @PostConstruct
    public void init() {
         this.s3Client = S3Client.builder()
                .region(Region.of(s3Properties.getRegion().getStaticRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        s3Properties.getCredentials().getAccessKey(),
                                        s3Properties.getCredentials().getSecretKey()
                                )
                        )
                )
                .build();
    }

    /**
     * S3에 이미지 업로드
     *
     * @param multipartFile 업로드할 이미지 파일
     * @param dir           업로드할 S3 디렉토리
     * @return 업로드된 이미지의 public URL
     * @throws IOException 업로드 실패 시
     */
    public String upload(MultipartFile multipartFile, String dir) throws IOException {
        String extension = getFileExtension(multipartFile.getOriginalFilename());
        String fileName = dir + "/" + UUID.randomUUID() + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.getS3().getBucket())
                .key(fileName)
                .contentType(multipartFile.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(
                multipartFile.getInputStream(),
                multipartFile.getSize()
        ));

        return getFileUrl(fileName);
    }

    /**
     * S3에서 파일 삭제
     *
     * @param fileUrl 전체 public URL
     */
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Properties.getS3().getBucket())
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    private String getFileUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.getS3().getBucket(),
                s3Properties.getRegion().getStaticRegion(),
                key
        );
    }

    private String getFileExtension(String filename) {
        return (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf("."))
                : "";
    }

    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
    }
}