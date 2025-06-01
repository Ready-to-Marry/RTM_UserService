package ready_to_marry.userservice.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.profile.dto.request.ProfileUpdateRequest;
import ready_to_marry.userservice.profile.dto.response.UserProfileResponse;
import ready_to_marry.userservice.profile.service.UserProfileService;

/**
 * 유저의 프로필 수정을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/users/me/profiles")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    /**
     * 현재 로그인한 유저의 프로필을 수정
     *
     * @param userId  게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param request name, phone, profileImage 중 일부 또는 전체를 포함한 유저 프로필 수정 요청 정보
     * @return 성공 시 code=0, data=null
     */
    @PatchMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Void>> updateMyProfile(@RequestHeader("X-User-Id") Long userId, @ModelAttribute ProfileUpdateRequest request) {
        userProfileService.updateMyProfile(userId, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("User profile updated successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인한 유저의 프로필을 조회
     *
     * @param userId 게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @return 성공 시 code=0, data=유저 프로필 정보
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(@RequestHeader("X-User-Id") Long userId) {
        UserProfileResponse profile = userProfileService.getMyProfile(userId);

        ApiResponse<UserProfileResponse> response = ApiResponse.<UserProfileResponse>builder()
                .code(0)
                .message("User profile retrieved successfully")
                .data(profile)
                .build();

        return ResponseEntity.ok(response);
    }
}