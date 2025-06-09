package woowacourse.shopping.domain.cart

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

    fun mostExpensiveCartPriceWithStandardQuantity(standardQuantity: Int): Int {
        return shoppingCarts
            .filter { it.quantity.value >= standardQuantity }
            .maxByOrNull { it.product.priceValue }
            ?.product
            ?.priceValue
            ?: throw IllegalArgumentException(CAN_NOT_FOUND_PRODUCT)
    }

    companion object {
        private const val CAN_NOT_FOUND_PRODUCT = "쿠폰에 적용할 수 있는 상품이 없습니다."
    }
}
