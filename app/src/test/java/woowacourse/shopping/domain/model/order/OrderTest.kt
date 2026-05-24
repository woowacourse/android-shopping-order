package woowacourse.shopping.domain.model.order

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.product.Product

class OrderTest {
    @Test
    fun `선택한 장바구니 상품만 주문으로 구성한다`() {
        val selectedProduct = purchaseProduct(id = 1L, price = 1_000, count = 2)
        val unselectedProduct = purchaseProduct(id = 2L, price = 10_000, count = 1)
        val cartItems = PurchaseProducts(listOf(selectedProduct, unselectedProduct))

        val order = Order.fromSelectedCartItems(
            cartItems = cartItems,
            selectedCartItemIds = listOf(1L),
        )

        order.purchaseProducts.purchaseProducts shouldBe listOf(selectedProduct)
        order.orderAmount shouldBe 2_000
    }

    private fun purchaseProduct(
        id: Long,
        price: Int,
        count: Int,
    ) =
        PurchaseProduct(
            id = id,
            product =
                Product(
                    category = "category",
                    id = id,
                    imageUri = "uri$id",
                    name = "상품$id",
                    price = price,
                ),
            count = count,
        )
}
