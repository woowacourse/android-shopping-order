package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import java.time.LocalDate

class BuyXGetYCouponTest {
    private val expirationDate = LocalDate.of(2026, 12, 31)
    private val coupon =
        BuyXGetYCoupon(
            id = 4,
            code = "BUY2GET1",
            description = "2개 사면 1개 공짜",
            expirationDate = expirationDate,
            buyQuantity = 2,
            getQuantity = 1,
        )

    private fun createOrder(purchaseProducts: List<PurchaseProduct>): Order =
        Order(
            purchaseProducts = purchaseProducts,
            currentTime = expirationDate.atStartOfDay(),
            isRemoteArea = false,
        )

    @Test
    fun `buyQuantity가 2일 때 2개를 사면 사용 가능해야 한다`() {
        val product = Product("카테고리", 1L, "uri", "상품", 1000)
        val purchaseProduct = PurchaseProduct(1, product, 2)
        val order = createOrder(listOf(purchaseProduct))

        assertTrue(coupon.isEligible(order))
    }

    @Test
    fun `조건에 맞는 상품들 중 가장 비싼 상품의 가격만큼 할인된다`() {
        val product1 = Product("카테고리", 1L, "uri", "싼상품", 1000)
        val product2 = Product("카테고리", 2L, "uri", "비싼상품", 5000)

        val purchaseProduct1 = PurchaseProduct(1, product1, 2)
        val purchaseProduct2 = PurchaseProduct(2, product2, 2)

        val order = createOrder(listOf(purchaseProduct1, purchaseProduct2))
        val discount = coupon.calculateDiscount(order)

        // buyQuantity(2) 이상인 상품 중 최댓값 5000 * getQuantity(1) = 5000
        assertEquals(5000, discount.productDiscount)
    }
}
