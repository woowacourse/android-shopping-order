package woowacourse.shopping.domain.product

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.Receipt
import woowacourse.shopping.domain.coupon.BoGoCoupon
import woowacourse.shopping.domain.coupon.Coupons
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CouponServiceTest {
    @Test
    fun `적용 가능한 쿠폰을 출력한다`() {

        val coupons = listOf(
            BoGoCoupon(
                couponId = 1L,
                description = "",
                expirationDate = LocalDate.of(2025, 11, 30),
                buyQuantity = 2,
                getQuantity = 1
            ),
            FixedCoupon(
                couponId = 2L,
                description = "",
                expirationDate = LocalDate.of(2025, 11, 30),
                disCountPrice = 5000,
                minimumOrderPrice = 100_000
            ),
            FreeShippingCoupon(
                couponId = 3L,
                description = "",
                expirationDate = LocalDate.of(2025, 11, 30),
                minimumOrderPrice = 50_000
            ),
            MiracleSaleCoupon(
                couponId = 4L,
                description = "",
                expirationDate = LocalDate.of(2025, 11, 30),
                startHour = LocalTime.of(4, 0),
                endHour = LocalTime.of(7, 0),
                discountRate = 30.0,
            )
        )

        val couponService = Coupons(
            coupons
        )

        val currentDateTime = LocalDateTime.of(2025, 11, 8, 5, 47)

        val receipt = Receipt(
            listOf(
                CartItem(
                    id = 1,
                    product = Product(
                        id = 1,
                        name = "밥",
                        price = 100_001,
                        category = "식료품",
                    ),
                    quantity = 3
                ),
                CartItem(
                    id = 2,
                    product = Product(
                        id = 2,
                        name = "국",
                        price = 200,
                        category = "식료품",
                    ),
                    quantity = 3
                )
            )
        )
        val actual = couponService.applyApplicableCoupons(receipt, currentDateTime)
        val expected = coupons

        assertThat(actual).isEqualTo(expected)
    }
}