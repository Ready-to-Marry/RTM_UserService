package ready_to_marry.userservice.fcm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.fcm.service.FcmTokenService;

/**
 * INTERNAL API 컨트롤러 - 유저 FCM 토큰 조회용
 */
@RestController
@RequestMapping("/internal/user-fcm-tokens")
@RequiredArgsConstructor
public class FcmTokenInternalController {
    private final FcmTokenService fcmTokenService;

    @GetMapping
    public ResponseEntity<ApiResponse<String>> getFcmToken(@RequestHeader("X-User-Id") Long userId) {
        String token = fcmTokenService.getInternalFcmToken(userId);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(0)
                .message("FCM token retrieved successfully")
                .data(token)
                .build();

        return ResponseEntity.ok(response);
    }
}