package ready_to_marry.userservice.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.profile.dto.response.InviteCodeIssueResponse;
import ready_to_marry.userservice.profile.service.UserProfileService;

/**
 * 유저의 초대 코드 발급을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/users/me/invite-code")
@RequiredArgsConstructor
public class UserInviteController {
    private final UserProfileService userProfileService;

    /**
     * 현재 로그인한 유저의 초대 코드를 발급
     *
     * @param userId 게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @return 성공 시 code=0, data=유저의 발급된 초대코드 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InviteCodeIssueResponse>> issueInviteCode(@RequestHeader("X-User-Id") Long userId) {
        InviteCodeIssueResponse inviteCodeResponse  = userProfileService.issueInviteCode(userId);

        ApiResponse<InviteCodeIssueResponse> response = ApiResponse.<InviteCodeIssueResponse>builder()
                .code(0)
                .message("Invite code issued successfully")
                .data(inviteCodeResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}