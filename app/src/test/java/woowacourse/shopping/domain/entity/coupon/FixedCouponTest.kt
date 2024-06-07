package woowacourse.shopping.domain.entity.coupon

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeCartProduct

class FixedCouponTest {
    @Test
    fun `최소 주문 금액이 10_000원일때, 총 주문 금액이 10_000원부터 할인 가능 하다`() {
        // given
        val cart = Cart(fakeCartProduct(price = 10_000))
        val coupon = fakeFixedCoupon(discount = 5_000, discountableMinPrice = 10_000)
        // when
        val available = coupon.available(cart, 0)
        val discount = coupon.discount(cart, 0)
        // then
        val expect = DiscountResult(10_000, 5_000, 0)
        assertSoftly {
            available.shouldBeTrue()
            discount shouldBe expect
        }
    }

    @Test
    fun `최소 주문 금액이 10_000원일때, 총 주문 금액이 9_999원이면 할인 불가능`() {
        // given
        val cart = Cart(fakeCartProduct(price = 9_999))
        val coupon = fakeFixedCoupon(discount = 5_000, discountableMinPrice = 10_000)
        // when
        val available = coupon.available(cart, 0)
        val discount = coupon.discount(cart, 0)
        // then
        val expect = DiscountResult(9_999, 0, 0)
        assertSoftly {
            available.shouldBeFalse()
            discount shouldBe expect
        }
    }
}