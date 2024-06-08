package woowacourse.shopping.domain.entity.coupon

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeCartProduct

class CouponsTest {
    @Test
    fun `사용가능한 쿠폰들을 반환한다`() {
        // given
        val cartProducts = listOf(fakeCartProduct(productId = 1L, price = 50_000))
        val cart = Cart(cartProducts)
        val coupons =
            fakeCoupons(
                fakePercentageCoupon(currentTime = 5, availableStartTime = 4, availableEndTime = 6),
                fakeBuyXGetYCoupon(buyCount = 0, freeCount = 1),
                fakeFixedCoupon(discountableMinPrice = 50_001),
                fakeFreeShippingCoupon(discountableMinPrice = 50_001),
            )
        // when
        val actual = coupons.availableCoupons(cart)
        // then
        val expect =
            fakeCoupons(
                fakePercentageCoupon(
                    currentTime = 5,
                    availableStartTime = 4,
                    availableEndTime = 6,
                ),
                fakeBuyXGetYCoupon(buyCount = 0, freeCount = 1),
            )
        actual shouldBe expect
    }
}
