@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.model.order

import org.junit.jupiter.api.Test
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.cart.CartItem
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.coupon.Discount
import woowacourse.shopping.model.product.Product
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class PaymentCalculatorTest {
    val items =
        listOf(
            CartItem(product = Product(name = "제품1", price = Money(5000), imageUrl = ""), quantity = 1),
        )
    val clock =
        Clock.fixed(
            LocalDateTime
                .of(2026, 5, 31, 12, 0)
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant(),
            ZoneId.of("Asia/Seoul"),
        )
    val calculator = PaymentCalculator()

    @Test
    fun `쿠폰이 없으면 할인은 0이고 배송비는 그대로 유지된다`() {
        // given
        val order =
            Order(
                items = items,
                selectedCoupon = null,
            )
        // when
        val actual = calculator.calculate(order, clock)
        // then
        assert(actual.couponDiscount == Money(0))
        assert(actual.shippingFee == order.shippingFee)
    }

    @Test
    fun `배송비 할인 쿠폰은 배송비에서 할인액을 차감한다`() {
        // given
        val coupon =
            Coupon.FreeShipping(
                id = null,
                code = "",
                description = "",
                expirationDate = LocalDate.of(2026, 6, 1),
                minimumAmount = Money(5000),
            )
        val order =
            Order(
                items = items,
                selectedCoupon = coupon,
            )
        val context = order.couponContext(clock)
        val discount = coupon.discount(context) as Discount.OnShipping
        // when
        val expected = order.shippingFee - discount.amount
        val actual = calculator.calculate(order, clock).shippingFee
        // then
        assert(expected == actual)
    }

    @Test
    fun `총액 할인 쿠폰은 할인액을 couponDiscount에 반영하고 배송비는 유지한다`() {
        // given
        val coupon =
            Coupon.FixedDiscount(
                id = null,
                code = "",
                description = "",
                expirationDate = LocalDate.of(2026, 6, 1),
                discount = Money(2000),
                minimumAmount = Money(5000),
            )
        val order = Order(items, coupon)
        val context = order.couponContext(clock)
        val discount = coupon.discount(context) as Discount.OnTotal
        // when
        val expected = discount.amount
        val actual = calculator.calculate(order, clock).couponDiscount
        // then
        assert(expected == actual)
    }
}
