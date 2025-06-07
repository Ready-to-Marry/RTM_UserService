package ready_to_marry.userservice.coupon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.NetworkException;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ready_to_marry.userservice.common.exception.ErrorCode;
import ready_to_marry.userservice.common.exception.InfrastructureException;
import ready_to_marry.userservice.coupon.dto.CouponRequest;

@RequiredArgsConstructor
@Service
public class CouponKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "coupon";

    public void sendCoupon(CouponRequest dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(TOPIC, json);
        } catch (JsonProcessingException e) {
            throw new InfrastructureException(ErrorCode.KAFKA_SERIALIZATION_ERROR, e);
        } catch (KafkaException | TimeoutException | NetworkException e) {
            throw new InfrastructureException(ErrorCode.KAFKA_CONNECTION_ERROR, e);
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.UNKNOWN_ERROR, e);
        }
    }
}
