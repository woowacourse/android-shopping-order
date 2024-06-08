package woowacourse.shopping.domain.entity.coupon

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeCartProduct

class FreeShippingCouponTest {
    @Test
    fun `상품들의 총 금액 합이 50_000원 일때  주문 금액이 50_000원 이면 할인 가능하다`() {
        // given
        val cartProduct = fakeCartProduct(price = 50_000, count = 1)
        val percentageCoupon = fakeFreeShippingCoupon(
            discountableMinPrice = 50_000
        )
        val cart = Cart(cartProduct)
        // when
        val available = percentageCoupon.available(cart)
        val actual = percentageCoupon.discount(cart, shippingFee = 3000)
        // then
        assertSoftly {
            available.shouldBeTrue()
            actual.discountPrice.shouldBe(3000)
            actual.paymentPrice.shouldBe(47_000)
        }
    }

    @Test
    fun `상품들의 총 금액 합이 50_000원 일때  주문 금액이 49_999원 이면 할인 불가능`() {
        // given
        val cartProduct = fakeCartProduct(price = 49_999, count = 1)
        val percentageCoupon = fakeFreeShippingCoupon(
            discountableMinPrice = 50_000
        )
        val cart = Cart(cartProduct)
        // when
        val available = percentageCoupon.available(cart)
        val actual = percentageCoupon.discount(cart, shippingFee = 3000)
        // then
        assertSoftly {
            available.shouldBeFalse()
            actual.discountPrice.shouldBe(0)
            actual.paymentPrice.shouldBe(52999L)
        }
    }

    @Test
    fun `쿠폰이 만료되면 할인이 적용 안된다`() {
        // given
        val percentageCoupon = fakePercentageCoupon(isExpired = true)
        val cart = Cart(fakeCartProduct(productId = 1, price = 2000, count = 1))
        // when
        val isExpired = percentageCoupon.isExpired
        val available = percentageCoupon.available(cart)
        // then
        assertSoftly {
            isExpired.shouldBeTrue()
            available.shouldBeFalse()
        }
    }
}