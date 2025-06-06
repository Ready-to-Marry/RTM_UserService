package ready_to_marry.userservice.notification.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * DynamoDB 커서 기반 페이징 요청 정보 DTO
 *
 * - page : 조회할 페이지 번호 (0부터 시작)
 * - size : 한 페이지당 조회할 데이터 개수 (최소 1)
 * - exclusiveStartKey : DynamoDB Query 시 “이 키 이후부터” 조회하기 위한 커서
 */
@Getter
@Setter
public class DynamoPagingRequest {
    // 조회할 페이지 번호 (0부터 시작)
    @Min(0)
    private int page = 0;

    // 한 페이지당 조회할 데이터 개수 (최소 1)
    @Min(1)
    private int size = 20;

    // DynamoDB 커서(ExclusiveStartKey)를 Base64/String 형태로 받은 값
    // 첫 요청 시에는 null로 보내면 DynamoDB가 처음부터 조회
    private String exclusiveStartKey;
}