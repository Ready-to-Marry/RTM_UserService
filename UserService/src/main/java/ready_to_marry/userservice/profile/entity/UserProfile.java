package ready_to_marry.userservice.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * user_db.user_profile 테이블 매핑 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_profile")
public class UserProfile {
    // PK 유저 도메인 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    // 커플 ID
    @Column(name = "couple_id")
    private UUID coupleId;

    // 유저 실명(또는 표시명)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    // 유저 연락처
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    // 프로필 사진 저장 주소
    @Column(name = "profile_img_url", length = 2048)
    private String profileImgUrl;

    // 유저 프로필 생성 시각
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;
}