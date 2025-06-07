package ready_to_marry.userservice.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ready_to_marry.userservice.coupon.dto.CouponRequest;
import ready_to_marry.userservice.coupon.service.CouponKafkaProducer;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponKafkaProducer couponKafkaProducer;

    @PostMapping
    public String registerCoupon(@RequestBody CouponRequest couponRequest) {
        couponKafkaProducer.sendCoupon(couponRequest);
        return "Kafka Coupon 전송 완료";
    }
}
