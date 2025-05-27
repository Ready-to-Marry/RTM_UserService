package ready_to_marry.userservice.common.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ready_to_marry.userservice.common.dto.response.ApiResponse;
import ready_to_marry.userservice.common.dto.response.ErrorDetail;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 오류 예외 처리
     * HTTP 200 + code>0
     * code = (1100~1199)
     * */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.ok(body);
    }

    /**
     * 검증 오류 예외 처리
     * HTTP 400 + code=400 + errors
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            ValidationException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleValidation(Exception ex) {
        List<ErrorDetail> errors = new ArrayList<>();

        if (ex instanceof MethodArgumentNotValidException manv) {
            // @Valid DTO 바인딩 에러
            for (FieldError fieldError : manv.getBindingResult().getFieldErrors()) {
                errors.add(ErrorDetail.builder()
                        .field(fieldError.getField())
                        .reason(fieldError.getDefaultMessage())
                        .build());
            }
        }
        else if (ex instanceof ConstraintViolationException cve) {
            // @Validated 메서드 파라미터 검증 실패
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                errors.add(ErrorDetail.builder()
                        .field(violation.getPropertyPath().toString())
                        .reason(violation.getMessage())
                        .build());
            }
        }
        else if (ex instanceof MethodArgumentTypeMismatchException mtm) {
            // 파라미터 타입 변환 실패 (e.g. ?id=abc)
            String field = mtm.getName();
            String msg = String.format("Parameter '%s' should be of type %s",
                    mtm.getName(),
                    mtm.getRequiredType().getSimpleName());
            errors.add(ErrorDetail.builder()
                    .field(field)
                    .reason(msg)
                    .build());
        }
        else if (ex instanceof ValidationException ve) {
            // 단일 필드 검증 실패
            errors.add(ErrorDetail.builder()
                    .field(ve.getField())
                    .reason(ve.getReason())
                    .build());
        }

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(400)
                .message("Bad Request")
                .data(null)
                .errors(errors)
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 인가 오류 예외 처리
     * HTTP 403 + code=403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(403)
                .message("Forbidden")
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * 리소스 없음 오류 예외 처리
     * HTTP 404 + code=404
     */
    @ExceptionHandler({
            NoSuchElementException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFound(RuntimeException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(404)
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * 인프라(시스템) 오류 예외 처리
     * HTTP 500 + code>0
     * code = (2100~2199)
     */
    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ApiResponse<Void>> handleInfrastructure(InfrastructureException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * 기타 서버 오류 예외 처리
     * HTTP 500 + code=500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled error", ex);
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(500)
                .message("Internal Server Error")
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}