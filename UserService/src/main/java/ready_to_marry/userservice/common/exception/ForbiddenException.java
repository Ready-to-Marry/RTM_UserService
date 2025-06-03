package ready_to_marry.userservice.common.exception;

import lombok.Getter;

/**
 * 보안 및 인가 예외를 나타내는 런타임 예외
 */
@Getter
public class ForbiddenException extends RuntimeException {
    private final int code;

    /**
     * @param errorCode 사전 정의된 에러 코드와 메시지를 담고 있는 {@link ErrorCode} enum
     */
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}
