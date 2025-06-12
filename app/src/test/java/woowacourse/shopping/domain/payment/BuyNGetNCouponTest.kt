package woowacourse.shopping.domain.payment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.payment.Coupon.BuyNGetNCoupon
import java.time.LocalDate

class BuyNGetNCouponTest {
    private val deliveryFee = 3000

    @Test
    fun `BOGO 쿠폰은 장바구니에 동일한 제품을 3개를 담은 상태에서 사용하면, 1개 분량의 금액을 할인한다`() {
        // given
        val coupon =
            BuyNGetNCoupon(
                id = 2,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = LocalDate.of(2024, 5, 30),
                buyQuantity = 2,
                getQuantity = 1,
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
        assertThat(discountAmount).isEqualTo(50_000)
    }

    @Test
    fun `3개씩 담은 제품이 여러개인 경우, 1개당 금액이 가장 비싼 제품에 적용한다`() {
        // given
        val coupon =
            BuyNGetNCoupon(
                id = 2,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = LocalDate.of(2024, 5, 30),
                buyQuantity = 2,
                getQuantity = 1,
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
        assertThat(discountAmount).isEqualTo(50_000)
    }
}
