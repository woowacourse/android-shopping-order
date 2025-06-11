package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageCoupon
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalTime

class FakeCouponRepository : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> {
        return Result.success(
            listOf(
                BuyXGetYCoupon(
                    id = 1,
                    code = "BOGO",
                    description = "2개 구매 시 1개 무료 쿠폰",
                    expirationDate = "2025-12-31",
                    buyQuantity = 2,
                    getQuantity = 1,
                ),
                FixedCoupon(
                    id = 2,
                    code = "FIXED5000",
                    description = "5,000원 할인 쿠폰",
                    expirationDate = "2025-12-31",
                    discount = 5000,
                    minimumAmount = 10000,
                ),
                FreeShippingCoupon(
                    id = 3,
                    code = "FREESHIPPING",
                    description = "5만원 이상 구매 시 무료 배송 쿠폰",
                    expirationDate = "2025-12-31",
                    minimumAmount = 50000,
                ),
                PercentageCoupon(
                    id = 4,
                    code = "MIRACLESALE",
                    description = "미라클모닝 30% 할인 쿠폰",
                    expirationDate = "2025-12-31",
                    discount = 20,
                    availableTime = AvailableTime(LocalTime.of(10, 0), LocalTime.of(18, 0)),
                ),
            ),
        )
    }
}
