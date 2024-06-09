package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalDate
import java.time.LocalTime

class FakeCouponRepository : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> {
        return runCatching {
            listOf(
                Coupon.Fixed(
                    id = 1,
                    code = "FIXED5000",
                    description = "5,000원 할인 쿠폰",
                    expirationDate = LocalDate.parse("2024-11-30"),
                    discount = 5000,
                    minimumAmount = 100000,
                    discountType = "fixed",
                ),
                Coupon.BuyXGetY(
                    id = 2,
                    code = "BOGO",
                    description = "2개 구매 시 1개 무료 쿠폰",
                    expirationDate = LocalDate.parse("2024-05-30"),
                    discountType = "buyXgetY",
                    buyQuantity = 2,
                    getQuantity = 1,
                ),
                Coupon.FreeShipping(
                    id = 3,
                    code = "FREESHIPPING",
                    description = "5만원 이상 구매 시 무료 배송 쿠폰",
                    expirationDate = LocalDate.parse("2024-08-31"),
                    minimumAmount = 50000,
                    discountType = "freeShipping",
                ),
                Coupon.MiracleSale(
                    id = 4,
                    code = "MIRACLESALE",
                    description = "미라클모닝 30% 할인 쿠폰",
                    expirationDate = LocalDate.parse("2024-07-31"),
                    discount = 30,
                    discountType = "percentage",
                    startTime = LocalTime.parse("04:00:00"),
                    endTime = LocalTime.parse("07:00:00"),
                ),
            )
        }
    }
}
