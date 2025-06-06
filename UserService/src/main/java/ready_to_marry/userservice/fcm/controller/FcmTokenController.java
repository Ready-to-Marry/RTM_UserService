package ready_to_marry.userservice.fcm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.fcm.dto.request.FcmTokenCreateOrUpdateRequest;
import ready_to_marry.userservice.fcm.service.FcmTokenService;

/**
 * 유저의 FCM 토큰 관련 기능을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/users/me/fcm-token")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    /**
     * 유저 FCM 토큰 등록/업데이트
     *
     * @param userId  게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @param request FCM 토큰 등록/업데이트 요청 정보
     * @return 성공 시 code=0, data=null
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveOrUpdateFcmToken(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody FcmTokenCreateOrUpdateRequest request) {
        fcmTokenService.saveOrUpdateToken(userId, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("FCM token created/updated successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 유저 FCM 토큰 삭제
     *
     * @param userId  게이트웨이가 파싱한 유저 도메인 ID (JWT에서 X-User-Id로 전달됨)
     * @return 성공 시 code=0, data=null
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteFcmToken(@RequestHeader("X-User-Id") Long userId) {
        fcmTokenService.deleteToken(userId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("FCM token deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
