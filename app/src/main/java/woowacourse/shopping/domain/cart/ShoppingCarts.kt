package woowacourse.shopping.domain.cart

import woowacourse.shopping.domain.product.Product
import java.io.Serializable

class ShoppingCarts(
    val shoppingCarts: List<ShoppingCart> = emptyList(),
) : Serializable {
    val cartIds: List<Long>
        get() = shoppingCarts.map { it.id }

    val totalPayment: Int
        get() = shoppingCarts.sumOf { it.payment }

    fun findProduct(productId: Long): ShoppingCart? {
        return shoppingCarts.find { it.productId == productId }
    }

    fun mostExpensiveCartWithStandardQuantity(standardQuantity: Int): Product? {
        return shoppingCarts
            .filter { it.quantity.value >= standardQuantity }
            .maxByOrNull { it.product.priceValue }
            ?.product
    }
}
