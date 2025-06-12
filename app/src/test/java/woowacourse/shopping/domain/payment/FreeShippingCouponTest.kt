package woowacourse.shopping.domain.payment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.CartItem
import java.time.LocalDate

class FreeShippingCouponTest {
    private val deliveryFee = 3000

    @Test
    fun `배송비 무료 쿠폰은 배송비만큼의 할인 금액을 제공한다`() {
        // given
        val coupon =
            Coupon.FreeShippingCoupon(
                id = 2,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = LocalDate.of(2024, 5, 30),
                minimumAmount = 50_000,
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
        assertThat(discountAmount).isEqualTo(3000)
    }

    @Test
    fun `배송비 무료 쿠폰은 최소 주문 금액을 갖는다`() {
        // given
        val coupon =
            Coupon.FreeShippingCoupon(
                id = 2,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = LocalDate.of(2024, 5, 30),
                minimumAmount = 50_000,
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
                            productPrice = 5_000,
                            category = "",
                            quantity = 3,
                        ),
                    ),
                fee = deliveryFee,
            )

        // then
        assertThat(discountAmount).isEqualTo(0)
    }
}
