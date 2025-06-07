package ready_to_marry.userservice.fcm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * user_db.fcm_token 테이블 매핑 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fcm_token")
public class FcmToken {
    // user_id 는 user_profile 테이블의 PK(user_id)와 매핑 (One-to-One 관계)
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    // FCM 토큰 문자열
    @Column(name = "fcm_token", length = 255, nullable = false)
    private String token;

    // 최초 저장 시각 (자동으로 채워짐)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // 마지막 갱신 시각 (자동으로 갱신)
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}