package ready_to_marry.userservice.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 유저의 커플 연결 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoupleConnectRequest {
    // 상대방이 발급한 초대 코드
    @NotBlank
    private String inviteCode;
}