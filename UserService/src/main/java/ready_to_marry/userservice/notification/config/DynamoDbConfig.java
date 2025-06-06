package ready_to_marry.userservice.notification.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ready_to_marry.userservice.common.config.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * AWS DynamoDB에 접근하기 위한 DynamoDbClient를 생성 및 설정하는 Configuration 클래스
 */
@Configuration
@RequiredArgsConstructor
public class DynamoDbConfig {
    private final AwsProperties awsProperties;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(awsProperties.getRegion().getStaticRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        awsProperties.getCredentials().getAccessKey(),
                                        awsProperties.getCredentials().getSecretKey()
                                )
                        )
                )
                .build();
    }
}