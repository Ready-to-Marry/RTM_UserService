package ready_to_marry.userservice.coupon.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponRequest {
    private Long userId;
    private String couponId;
}
