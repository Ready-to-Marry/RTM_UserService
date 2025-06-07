package ready_to_marry.userservice.profile.dto.response;

import lombok.*;

/**
 * 초대 코드 발급 결과 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteCodeIssueResponse {
    // 발급된 초대 코드
    private String inviteCode;
}