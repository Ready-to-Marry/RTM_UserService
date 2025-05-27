package ready_to_marry.userservice.common.exception;

import lombok.Getter;

/**
 * 서비스 로직 내부에서 발생하는 단일 필드 검증 오류를 나타내는 런타임 예외
 */
@Getter
public class ValidationException extends RuntimeException {
    private final String field;
    private final String reason;

    /**
     * @param field 검증에 실패한 필드 이름
     * @param reason 해당 필드의 검증 실패 사유
     */
    public ValidationException(String field, String reason) {
        super(String.format("Validation failed: %s - %s", field, reason));
        this.field = field;
        this.reason = reason;
    }
}