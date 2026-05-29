@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.domain.model.order

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.domain.model.cart.SelectedCartOrderItem
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.OrderFixedAmountDiscountPolicy
import java.time.LocalDate

class OrderPriceSummaryTest {
    @Test
    fun `쿠폰이 없으면 기본 배송비 3000원이 포함된다`() {
        val summary = selectedCartOrder(totalPrice = 7_500L).calculatePriceSummary()

        assertEquals(7_500L, summary.orderAmount)
        assertEquals(0L, summary.couponDiscount)
        assertEquals(3_000L, summary.deliveryFee)
        assertEquals(10_500L, summary.totalPaymentPrice)
    }

    @Test
    fun `쿠폰 할인 금액은 주문 금액을 초과할 수 없다`() {
        val summary =
            selectedCartOrder(totalPrice = 5_000L).calculatePriceSummary(
                selectedCoupon = fixedAmountCoupon(discountAmount = 10_000),
            )

        assertEquals(5_000L, summary.orderAmount)
        assertEquals(5_000L, summary.couponDiscount)
        assertEquals(3_000L, summary.deliveryFee)
        assertEquals(3_000L, summary.totalPaymentPrice)
    }

    @Test
    fun `최종 결제 금액은 0원 아래로 내려가지 않는다`() {
        val summary =
            selectedCartOrder(totalPrice = 5_000L).calculatePriceSummary(
                selectedCoupon = fixedAmountCoupon(discountAmount = 10_000),
                defaultDeliveryFee = -10_000L,
            )

        assertEquals(5_000L, summary.orderAmount)
        assertEquals(5_000L, summary.couponDiscount)
        assertEquals(-10_000L, summary.deliveryFee)
        assertEquals(0L, summary.totalPaymentPrice)
    }

    private fun selectedCartOrder(totalPrice: Long): SelectedCartOrder =
        SelectedCartOrder(
            items =
                listOf(
                    SelectedCartOrderItem(
                        cartItemId = 1L,
                        productId = 1L,
                        price = totalPrice.toInt(),
                        quantity = 1,
                    ),
                ),
        )

    private fun fixedAmountCoupon(discountAmount: Int): Coupon =
        Coupon(
            id = 1L,
            code = "FIXED",
            title = "정액 할인",
            description = "",
            expirationDate = LocalDate.of(2026, 12, 31),
            policy = OrderFixedAmountDiscountPolicy(amount = discountAmount),
        )
}
