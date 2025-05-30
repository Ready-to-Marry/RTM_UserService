package ready_to_marry.userservice.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.profile.dto.request.CoupleConnectRequest;
import ready_to_marry.userservice.profile.dto.response.InviteCodeIssueResponse;
import ready_to_marry.userservice.profile.service.UserProfileService;

/**
 * 유저의 커플 관련 기능을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserCoupleController {
    private final UserProfileService userProfileService;

    /**
     * 현재 로그인한 유저의 초대 코드를 발급
     *
     * @param userId 게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @return 성공 시 code=0, data=유저의 발급된 초대코드 정보
     */
    @PostMapping("/invite-code")
    public ResponseEntity<ApiResponse<InviteCodeIssueResponse>> issueInviteCode(@RequestHeader("X-User-Id") Long userId) {
        InviteCodeIssueResponse inviteCodeResponse  = userProfileService.issueInviteCode(userId);

        ApiResponse<InviteCodeIssueResponse> response = ApiResponse.<InviteCodeIssueResponse>builder()
                .code(0)
                .message("Invite code issued successfully")
                .data(inviteCodeResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 초대 코드 기반으로 현재 로그인한 유저의 커플 연결을 수행
     *
     * @param userId   게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param request  초대 코드가 포함된 유저의 커플 연결 요청 정보
     * @return 성공 시 code=0, data=null
     */
    @PostMapping("/couple-connection")
    public ResponseEntity<ApiResponse<Void>> connectCouple(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid CoupleConnectRequest request) {
        userProfileService.connectCouple(userId, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Couple connected successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인한 유저의 커플 연결을 해제
     *
     * @param userId   게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @return 성공 시 code=0, data=null
     */
    @PostMapping("/couple-connection/release")
    public ResponseEntity<ApiResponse<Void>> releaseCouple(@RequestHeader("X-User-Id") Long userId) {
        userProfileService.releaseCouple(userId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Couple released successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}