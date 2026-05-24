package woowacourse.shopping.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.coupon.Discount
import java.time.LocalDateTime

class OrderTest {
    private val product1 =
        Product(
            id = 1L,
            name = "상품1",
            price = 10000,
            imageUri = "uri1",
            category = "카테고리1",
        )
    private val product2 =
        Product(
            id = 2L,
            name = "상품2",
            price = 20000,
            imageUri = "uri2",
            category = "카테고리2",
        )

    private val purchaseProduct1 = PurchaseProduct(id = 10L, product = product1, count = 2) // 20000
    private val purchaseProduct2 = PurchaseProduct(id = 20L, product = product2, count = 1) // 20000

    @Test
    fun `상품들의 총 가격을 계산할 수 있다`() {
        // given
        val order =
            Order(
                purchaseProducts = listOf(purchaseProduct1, purchaseProduct2),
                currentTime = LocalDateTime.now(),
                isRemoteArea = false,
            )

        // when
        val totalProductPrice = order.totalProductPrice

        // then
        assertEquals(40000, totalProductPrice)
    }

    @Test
    fun `배송비와 할인을 포함한 최종 결제 금액을 계산할 수 있다`() {
        // given
        val order =
            Order(
                purchaseProducts = listOf(purchaseProduct1, purchaseProduct2),
                shippingFee = 3000,
                currentTime = LocalDateTime.now(),
                isRemoteArea = false,
            )
        val discount = Discount(productDiscount = 5000, shippingDiscount = 1000)

        // when
        val finalPrice = order.calculateFinalPrice(discount)

        // then
        assertEquals(37000, finalPrice)
    }
}
