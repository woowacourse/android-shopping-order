package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import java.time.LocalDate
import java.time.LocalDateTime

class FreeShippingCouponTest {
    private val expirationDate = LocalDate.of(2026, 12, 31)
    private val coupon = FreeShippingCoupon(
        id = 3,
        code = "FREESHIP",
        description = "무료 배송",
        expirationDate = expirationDate,
        minimumAmount = 15000
    )

    private fun createOrder(totalPrice: Int, shippingFee: Int): Order {
        val product = Product("카테고리", 1L, "uri", "상품", totalPrice)
        val purchaseProduct = PurchaseProduct(1, product, 1)
        return Order(
            purchaseProducts = listOf(purchaseProduct),
            shippingFee = shippingFee,
            currentTime = expirationDate.atStartOfDay(),
            isRemoteArea = false
        )
    }

    @Test
    fun `최소 주문 금액 이상이면 사용 가능하다`() {
        val order = createOrder(15000, 3000)
        assertTrue(coupon.isEligible(order))
    }

    @Test
    fun `최소 주문 금액 미만이면 사용 불가능하다`() {
        val order = createOrder(14900, 3000)
        assertFalse(coupon.isEligible(order))
    }

    @Test
    fun `배송비만큼 할인된다`() {
        val order = createOrder(20000, 3000)
        val discount = coupon.calculateDiscount(order)
        assertEquals(0, discount.productDiscount)
        assertEquals(3000, discount.shippingDiscount)
    }
}
