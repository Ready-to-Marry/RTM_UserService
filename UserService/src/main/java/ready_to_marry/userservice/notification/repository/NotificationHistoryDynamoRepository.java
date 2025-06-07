package ready_to_marry.userservice.notification.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ready_to_marry.userservice.common.config.AwsProperties;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.common.util.MaskingUtils;
import ready_to_marry.userservice.notification.dto.request.DynamoPagingRequest;
import ready_to_marry.userservice.notification.dto.response.NotificationHistoryResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationHistoryDynamoRepository implements NotificationHistoryRepository {
    private final DynamoDbClient dynamoDbClient;
    private final AwsProperties awsProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<NotificationHistoryResponse> findByUserId(String userId, DynamoPagingRequest pagingRequest) {
        // 1) QueryRequest 빌더 생성
        userId = "user" + userId;
        QueryRequest.Builder queryBuilder = QueryRequest.builder()
                .tableName(awsProperties.getDynamodb().getTableName())
                .keyConditionExpression("id = :userId")
                .expressionAttributeValues(Map.of(":userId", AttributeValue.fromS(userId)))
                .scanIndexForward(false)               // 최신순 정렬
                .limit(pagingRequest.getSize());       // 페이지 크기만큼만 조회

        // 2) 클라이언트가 보낸 exclusiveStartKey(Base64 String)가 있으면 디코딩해서 ExclusiveStartKey로 설정
        if (pagingRequest.getExclusiveStartKey() != null && !pagingRequest.getExclusiveStartKey().isBlank()) {
            Map<String, AttributeValue> eks = decodeExclusiveStartKey(pagingRequest.getExclusiveStartKey());
            queryBuilder.exclusiveStartKey(eks);
        }

        QueryResponse response;
        try {
            // 3) 실제 DynamoDB 쿼리 실행
            response = dynamoDbClient.query(queryBuilder.build());
        } catch (Exception ex) {
            log.error("{}: identifierType=userId, identifierValue={}", ErrorCode.DB_RETRIEVE_FAILURE.getMessage(), MaskingUtils.maskUserId(Long.parseLong(userId)), ex);
            throw new InfrastructureException(ErrorCode.DB_RETRIEVE_FAILURE, ex);
        }

        // 4) 조회된 아이템을 DTO로 매핑
        List<NotificationHistoryResponse> items = response.items().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // 5) LastEvaluatedKey를 Base64 직렬화하여 리스트 마지막 DTO에 넣어준다
        Map<String, AttributeValue> lastEvaluatedKey = response.lastEvaluatedKey();
        if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
            String nextCursor = encodeExclusiveStartKey(lastEvaluatedKey);
            if (!items.isEmpty()) {
                // 리스트의 마지막 아이템에만 nextExclusiveStartKey 필드를 채워서 반환
                NotificationHistoryResponse lastDto = items.get(items.size() - 1);
                lastDto.setNextExclusiveStartKey(nextCursor);
            }
        }

        return items;
    }

    /**
     * DynamoDB Item(Map<String, AttributeValue>) → NotificationHistoryResponse DTO 변환
     */
    private NotificationHistoryResponse mapToResponse(Map<String, AttributeValue> item) {
        return NotificationHistoryResponse.builder()
                .createdAt(item.get("createdAt").s())
                .title(item.getOrDefault("title", AttributeValue.fromS("")).s())
                .message(item.getOrDefault("message", AttributeValue.fromS("")).s())
                .amount(item.getOrDefault("amount", AttributeValue.fromN("0")).n())
                .contractId(item.getOrDefault("contractId", AttributeValue.fromN("0")).n())
                .status(item.getOrDefault("status", AttributeValue.fromS("")).s())
                .build();
    }

    /**
     * LastEvaluatedKey(Map<String, AttributeValue>)를 JSON으로 바꾸고 Base64 인코딩
     * → 클라이언트에게 nextExclusiveStartKey로 전달
     */
    private String encodeExclusiveStartKey(Map<String, AttributeValue> lastEvaluatedKey) {
        try {
            // AttributeValue 맵을 simpleMap(Map<String, String>)으로 바꾸자
            Map<String, String> simpleMap = lastEvaluatedKey.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> {
                                AttributeValue v = e.getValue();
                                if (v.s() != null) return v.s();
                                if (v.n() != null) return v.n();
                                // (필요시 다른 타입 처리)
                                return "";
                            }
                    ));
            String json = objectMapper.writeValueAsString(simpleMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException ex) {
            log.error("{}: identifierType=exclusiveStartKey, identifierValue={}", ErrorCode.EXCLUSIVE_KEY_ENCODING_FAILURE.getMessage(), MaskingUtils.maskExclusiveStartKey(lastEvaluatedKey.toString()), ex);
            throw new InfrastructureException(ErrorCode.EXCLUSIVE_KEY_ENCODING_FAILURE, ex);
        }
    }

    /**
     * 클라이언트가 보낸 Base64(String) → JSON(Map<String, String>) → Map<String, AttributeValue> 역직렬화
     */
    private Map<String, AttributeValue> decodeExclusiveStartKey(String cursor) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            String json = new String(decodedBytes, StandardCharsets.UTF_8);
            Map<String, String> simpleMap = objectMapper.readValue(json, new TypeReference<>() {});
            return simpleMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> AttributeValue.fromS(e.getValue())
                    ));
        } catch (Exception ex) {
            log.error("{}: identifierType=exclusiveStartKey, identifierValue={}", ErrorCode.EXCLUSIVE_KEY_DECODING_FAILURE.getMessage(), MaskingUtils.maskExclusiveStartKey(cursor), ex);
            throw new InfrastructureException(ErrorCode.EXCLUSIVE_KEY_DECODING_FAILURE, ex);
        }
    }
}
