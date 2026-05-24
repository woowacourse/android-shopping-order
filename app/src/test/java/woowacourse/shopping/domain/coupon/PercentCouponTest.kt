package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PercentCouponTest {
    private val expirationDate = LocalDate.of(2026, 12, 31)
    private val coupon = PercentCoupon(
        id = 2,
        code = "PERCENT10",
        description = "10% 할인",
        expirationDate = expirationDate,
        discountPercent = 0.1,
        startTime = LocalTime.of(10, 0),
        endTime = LocalTime.of(22, 0)
    )

    private fun createOrder(totalPrice: Int, currentTime: LocalDateTime): Order {
        val product = Product("카테고리", 1L, "uri", "상품", totalPrice)
        val purchaseProduct = PurchaseProduct(1, product, 1)
        return Order(
            purchaseProducts = listOf(purchaseProduct),
            currentTime = currentTime,
            isRemoteArea = false
        )
    }

    @Test
    fun `사용 가능 시간대 내이고 유효기간 이내이면 사용 가능하다`() {
        val order = createOrder(10000, expirationDate.atTime(15, 0))
        assertTrue(coupon.isEligible(order))
    }

    @Test
    fun `사용 가능 시간 이전에는 사용 불가능하다`() {
        val order = createOrder(10000, expirationDate.atTime(9, 59))
        assertFalse(coupon.isEligible(order))
    }

    @Test
    fun `사용 가능 시간 이후에는 사용 불가능하다`() {
        val order = createOrder(10000, expirationDate.atTime(22, 1))
        assertFalse(coupon.isEligible(order))
    }

    @Test
    fun `정해진 비율만큼 할인된다`() {
        val order = createOrder(20000, expirationDate.atTime(15, 0))
        val discount = coupon.calculateDiscount(order)
        assertEquals(2000, discount.productDiscount)
    }
}
