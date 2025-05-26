package ready_to_marry.userservice.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Internal API를 통해 전달되는 유저 프로필 등록 요청 DTO
 *
 * Auth Service가 소셜 로그인 후 User Service에 프로필 생성 요청을
 * 보낼 때 사용하는 내부용 데이터 모델
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalProfileCreateRequest {
    // 유저 실명(또는 표시명)
    @NotBlank
    @Size(max = 50)
    private String name;

    // 유저 연락처: 맨 앞에 + 가 0~1회 올 수 있고 그 뒤에는 숫자나 하이픈만 조합
    @NotBlank
    @Pattern(regexp = "^\\+?[0-9\\-]{1,20}$")
    private String phone;
}