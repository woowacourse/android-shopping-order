package woowacourse.shopping.domain.payment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.payment.Coupon.FixedDiscountCoupon
import java.time.LocalDate

class FixedDiscountCouponTest {
    private val deliveryFee = 3000

    @Test
    fun `고정 금액 할인 쿠폰은 고정된 할인 금액을 제공한다`() {
        // given
        val coupon =
            FixedDiscountCoupon(
                id = 1,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = LocalDate.of(2024, 11, 30),
                discount = 5_000,
                minimumAmount = 100_000,
            )

        // when
        val discountAmount =
            coupon.discountAmount(
                cartItems =
                    listOf(
                        CartItem(
                            id = 0,
                            productId = 0,
                            productName = "",
                            productPrice = 50_000,
                            category = "",
                            quantity = 3,
                        ),
                    ),
                fee = deliveryFee,
            )

        // then
        assertThat(discountAmount).isEqualTo(5_000)
    }

    @Test
    fun `고정 금액 할인 쿠폰은 최소 주문 금액을 갖는다`() {
        // given
        val coupon =
            FixedDiscountCoupon(
                id = 1,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = LocalDate.of(2024, 11, 30),
                discount = 5_000,
                minimumAmount = 100_000,
            )

        // when
        val discountAmount =
            coupon.discountAmount(
                cartItems =
                    listOf(
                        CartItem(
                            id = 0,
                            productId = 0,
                            productName = "",
                            productPrice = 50_000,
                            category = "",
                            quantity = 1,
                        ),
                    ),
                fee = deliveryFee,
            )

        // then
        assertThat(discountAmount).isEqualTo(0)
    }
}
