@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.model.order

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import woowacourse.shopping.model.Money

class PaymentTest {
    @Test
    fun `finalAmount는 subtotal에서 couponDiscount를 빼고 shippingFee를 더한 값이다`() {
        // given
        val payment = Payment(
            subtotal = Money(20000),
            couponDiscount = Money(3000),
            shippingFee = Money(3000)
        )
        // when
        val expected = payment.subtotal - payment.couponDiscount + payment.shippingFee
        val actual = payment.finalAmount

        // then
        assert(expected == actual)
    }

    @Test
    fun `couponDiscount가 subtotal보다 크면 finalAmount 값을 읽을 때 오류가 발생한다`() {
        // given
        val payment = Payment(
            subtotal = Money(10000),
            couponDiscount = Money(11000),
            shippingFee = Money(3000)
        )
        // when & then
        assertThrows<IllegalArgumentException> {
            payment.finalAmount
        }
    }
}