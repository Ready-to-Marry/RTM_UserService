package ready_to_marry.userservice.profile.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * application.properties의 초대 코드 관련 설정을 바인딩
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "invite-code")
public class InviteCodeProperties {
    // 초대 코드 TTL
    private Duration ttl;
}