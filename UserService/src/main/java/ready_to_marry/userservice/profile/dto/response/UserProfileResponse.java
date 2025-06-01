package ready_to_marry.userservice.profile.dto.response;

import lombok.*;

/**
 * 로그인한 유저의 프로필 조회 결과 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    // 유저 실명(또는 표시명)
    private String name;

    // 유저 연락처
    private String phone;

    // 프로필 사진 저장 주소
    private String profileImgUrl;

    // 커플 연결 여부
    private boolean connectedCouple;
}
