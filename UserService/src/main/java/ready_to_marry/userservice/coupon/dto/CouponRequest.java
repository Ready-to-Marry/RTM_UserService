package ready_to_marry.userservice.coupon.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponRequest {
    private Long userId;
    private String couponCode;
    private String couponName;
    private String couponContent;
    private Long couponPrice;
    private LocalDateTime issuedAt;
}
