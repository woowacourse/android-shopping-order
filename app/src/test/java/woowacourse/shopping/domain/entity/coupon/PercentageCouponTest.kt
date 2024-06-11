package woowacourse.shopping.domain.entity.coupon

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeCartProduct

class PercentageCouponTest {
    @Test
    fun `할인 가능한 시간이면, 할인율이 적용된다`() {
        // given
        val percentageCoupon =
            fakePercentageCoupon(
                isExpired = false,
                currentTime = 5,
                availableStartTime = 4,
                availableEndTime = 7,
            )
        val cart = Cart(fakeCartProduct(productId = 1, price = 2000, count = 1))
        // when
        val available = percentageCoupon.available(cart, fakeTargetDateTime(5))
        // then
        available.shouldBeTrue()
    }

    @Test
    fun `할인 가능한 시간이 아니면, 할인율이 적용 안된다`() {
        // given
        val percentageCoupon =
            fakePercentageCoupon(
                isExpired = false,
                currentTime = 3,
                availableStartTime = 4,
                availableEndTime = 7,
            )
        val cart = Cart(fakeCartProduct(productId = 1, price = 2000, count = 1))
        // when
        val available = percentageCoupon.available(cart, fakeTargetDateTime(3))
        // then
        available.shouldBeFalse()
    }

    @Test
    fun `쿠폰이 만료되면 할인이 적용 안된다`() {
        // given
        val percentageCoupon = fakePercentageCoupon(isExpired = true)
        val cart = Cart(fakeCartProduct(productId = 1, price = 2000, count = 1))
        // when
        val available = percentageCoupon.available(cart, fakeTargetDateTime(5))
        // then
        assertSoftly {
            available.shouldBeFalse()
        }
    }

    @Test
    fun `총 주문 금액이 1000원이고 할인율이 30% 이면, 300원 할인된다`() {
        // given
        val percentageCoupon = fakePercentageCoupon(isExpired = false)
        val cart = Cart(fakeCartProduct(productId = 1, price = 1000, count = 1))
        // when
        val available = percentageCoupon.available(cart, fakeTargetDateTime(5))
        val actual = percentageCoupon.discount(cart, shippingFee = 0, fakeTargetDateTime(5))
        // then
        val expect =
            DiscountResult(
                orderPrice = 1000L,
                discountPrice = 300L,
                shippingFee = 0L,
            )
        assertSoftly {
            available.shouldBeTrue()
            actual shouldBe expect
        }
    }
}
