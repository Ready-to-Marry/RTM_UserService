package ready_to_marry.userservice.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.profile.dto.request.InternalProfileCreateRequest;
import ready_to_marry.userservice.profile.service.UserProfileService;

/**
 * INTERNAL API 컨트롤러 - 유저 프로필 등록용
 */
@RestController
@RequestMapping("/internal/user-profiles")
@RequiredArgsConstructor
public class UserProfileInternalController {
    private final UserProfileService userProfileService;

    /**
     * 최소 정보(name, phone)로 유저 프로필 등록
     *
     * @param request 유저 프로필 등록 요청 정보
     * @return 성공 시 code=0, data=생성된 유저의 도메인 ID 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProfile(@Valid @RequestBody InternalProfileCreateRequest request) {
        Long result = userProfileService.createInternalProfile(request);

        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .code(0)
                .message("User profile created successfully")
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }
}
