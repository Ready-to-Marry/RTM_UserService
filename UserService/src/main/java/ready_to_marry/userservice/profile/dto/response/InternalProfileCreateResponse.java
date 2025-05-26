package ready_to_marry.userservice.profile.dto.response;

import lombok.*;

/**
 * Internal API 유저 프로필 등록 결과 응답 DTO
 *
 * Auth Service가 소셜 로그인 후 User Service에 프로필 생성 요청을 보낼 때,
 * 성공적으로 생성된 유저의 도메인 ID를 담아 반환하는 내부용 데이터 모델
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalProfileCreateResponse {
    // 유저 도메인 ID
    private Long userId;
}
