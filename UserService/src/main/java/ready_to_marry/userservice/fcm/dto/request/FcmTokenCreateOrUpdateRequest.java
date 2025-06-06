package ready_to_marry.userservice.fcm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 유저의 FCM 토큰 등록/업데이트 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmTokenCreateOrUpdateRequest {
    // FCM 토큰 문자열
    @NotBlank
    private String token;
}