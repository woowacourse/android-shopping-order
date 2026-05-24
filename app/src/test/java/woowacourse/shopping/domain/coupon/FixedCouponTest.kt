package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import java.time.LocalDate
import java.time.LocalDateTime

class FixedCouponTest {
    private val expirationDate = LocalDate.of(2026, 12, 31)
    private val coupon =
        FixedCoupon(
            id = 1,
            code = "FIXED1000",
            description = "1000원 할인",
            expirationDate = expirationDate,
            discountAmount = 1000,
            minimumAmount = 10000,
        )

    private fun createOrder(
        totalPrice: Int,
        currentTime: LocalDateTime,
    ): Order {
        val product = Product("카테고리", 1L, "uri", "상품", totalPrice)
        val purchaseProduct = PurchaseProduct(1, product, 1)
        return Order(
            purchaseProducts = listOf(purchaseProduct),
            currentTime = currentTime,
            isRemoteArea = false,
        )
    }

    @Test
    fun `최소 주문 금액 이상이고 유효기간 이내이면 사용 가능하다`() {
        val order = createOrder(10000, expirationDate.atStartOfDay())
        assertTrue(coupon.isEligible(order))
    }

    @Test
    fun `최소 주문 금액 미만이면 사용 불가능하다`() {
        val order = createOrder(9999, expirationDate.atStartOfDay())
        assertFalse(coupon.isEligible(order))
    }

    @Test
    fun `유효기간이 지나면 사용 불가능하다`() {
        val order = createOrder(10000, expirationDate.plusDays(1).atStartOfDay())
        assertFalse(coupon.isEligible(order))
    }

    @Test
    fun `정해진 금액만큼 정확히 할인된다`() {
        val order = createOrder(15000, expirationDate.atStartOfDay())
        val discount = coupon.calculateDiscount(order)
        assertEquals(1000, discount.productDiscount)
        assertEquals(0, discount.shippingDiscount)
    }
}
