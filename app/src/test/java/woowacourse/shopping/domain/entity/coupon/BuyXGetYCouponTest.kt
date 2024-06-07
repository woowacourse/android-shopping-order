package woowacourse.shopping.domain.entity.coupon

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeCartProduct

class BuyXGetYCouponTest {
    @Test
    fun `2 + 1 할인일 때, 3개 이상 주문해야 할인이 가능하다`() {
        // given
        val cart = Cart(fakeCartProduct(price = 1000, count = 3))
        val buyXGetYCoupon = fakeBuyXGetYCoupon(
            buyCount = 2,
            freeCount = 1
        )
        // when
        val actual = buyXGetYCoupon.available(cart, 200)
        // then
        assertSoftly {
            actual.shouldBeTrue()
        }
    }

    @Test
    fun `2 + 1 할인일 때, 3개 주문하면 할인이 안된다`() {
        // given
        val cart = Cart(fakeCartProduct(price = 1000, count = 2))
        val buyXGetYCoupon = fakeBuyXGetYCoupon(
            buyCount = 2,
            freeCount = 1
        )
        // when
        val actual = buyXGetYCoupon.available(cart, 200)
        // then
        assertSoftly {
            actual.shouldBeFalse()
        }
    }

    @Test
    fun `할인 가능한 제품이 여러개일 경우, 가장 비싼 제품에 할인이 적용된다`() {
        // given
        val cart = Cart(
            fakeCartProduct(price = 1000, count = 3),
            fakeCartProduct(price = 2000, count = 3),
            fakeCartProduct(price = 3000, count = 3)
        )
        val buyXGetYCoupon = fakeBuyXGetYCoupon(
            buyCount = 2,
            freeCount = 1
        )
        // when
        val actual = buyXGetYCoupon.discount(cart, 200)
        // then
        val expect = DiscountResult(18_000, 3000, 200)
        val expectPayment = 15_200
        assertSoftly {
            expect shouldBe actual
            expectPayment shouldBe actual.paymentPrice
        }
    }
}