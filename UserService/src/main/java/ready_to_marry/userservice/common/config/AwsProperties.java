package ready_to_marry.userservice.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * application.properties의 S3, DynamoDB 설정을 바인딩
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
    // S3 업로드에 사용할 버킷 이름 설정
    private S3 s3;

    // DynamoDB 조회할 테이블 이름 설정
    private Dynamodb dynamodb;

    // AWS 액세스 키 및 시크릿 키 설정
    private Credentials credentials;

    // S3가 위치한 AWS 리전(region) 설정
    private Region region;

    @Getter
    @Setter
    public static class S3 {
        private String bucket;
    }

    @Getter
    @Setter
    public static class Dynamodb {
        private String tableName;
    }

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class Region {
        private String staticRegion;
    }
}