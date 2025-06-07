package woowacourse.shopping.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.OrderPriceSummary
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.fixture.CouponFixture
import woowacourse.shopping.fixture.ProductsFixture

class OrderPriceSummaryTest {
    private val cartItems =
        listOf(
            CartItem(
                product = ProductsFixture.dummyProduct.copy(_price = Price(10_000)),
                quantity = 3,
            ),
            CartItem(
                product = ProductsFixture.dummyProduct.copy(_price = Price(30_000)),
                quantity = 6,
            ),
        )

    @Test
    fun `최종_결제_금액은_전체_금액에서_할인금액을_뺀_후_배송비를_더한_금액이어야_한다`() {
        val summary =
            OrderPriceSummary(
                productTotalPrice = 50_000,
                discountAmount = 5_000,
                deliveryFee = 3_000,
                cartItems = emptyList(),
            )

        assertThat(summary.finalPrice).isEqualTo(48_000)
    }

    @Test
    fun `고정_금액_할인_쿠폰을_적용하면_할인_금액만큼_최종_결제_금액이_감소해야_한다`() {
        val coupon = CouponFixture.fixedDummyCoupon

        val summary =
            OrderPriceSummary(
                productTotalPrice = 210_000,
                cartItems = cartItems,
            ).applyCoupon(coupon)

        assertThat(summary.finalPrice).isEqualTo(203_000)
    }

    @Test
    fun `퍼센트_할인_쿠폰을_적용하면_비율에_맞는_금액만큼_최종_결제_금액이_감소해야_한다`() {
        val coupon = CouponFixture.percentDummyCoupon

        val summary =
            OrderPriceSummary(
                productTotalPrice = 210_000,
                cartItems = emptyList(),
            ).applyCoupon(coupon)

        assertThat(summary.finalPrice).isEqualTo(150_000)
    }

    @Test
    fun `무료_배송_쿠폰을_적용하면_배송비가_0원이다`() {
        val coupon = CouponFixture.freeShippingDummyCoupon

        val summary =
            OrderPriceSummary(
                productTotalPrice = 210_000,
                cartItems = emptyList(),
            ).applyCoupon(coupon)

        assertThat(summary.deliveryFee).isEqualTo(0)
    }

    @Test
    fun `buyXgetY_쿠폰을_적용하면_가장_비싼_상품_금액만큼_할인된다`() {
        val coupon = CouponFixture.buyXgetYDummyCoupon

        val summary =
            OrderPriceSummary(
                productTotalPrice = 210_000,
                cartItems = cartItems,
            ).applyCoupon(coupon)

        // 10_000원 3개, 30_000원 6개이므로, 3만원짜리 상품 할인 됨
        assertThat(summary.finalPrice).isEqualTo(183_000)
    }
}
