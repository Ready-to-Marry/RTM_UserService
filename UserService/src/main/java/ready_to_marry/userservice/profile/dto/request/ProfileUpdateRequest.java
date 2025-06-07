package ready_to_marry.userservice.profile.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 유저의 프로필 수정 요청 DTO (multipart/form-data 전용)
 *
 * name, phone, profileImage 중 존재하는 필드만 수정
 * 빈 값(null)인 경우 해당 필드는 수정하지 않음 (null 체크 기반 머지)
 * 별도 검증 로직은 서비스 내부에서 처리 (전화번호 정규식, 파일 유효성 등)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {
    // 유저 실명(또는 표시명) (선택 수정)
    private String name;

    // 유저 연락처 (선택 수정)
    private String phone;

    // 프로필 이미지 파일 (선택 수정, S3 업로드 대상)
    private MultipartFile profileImage;
}