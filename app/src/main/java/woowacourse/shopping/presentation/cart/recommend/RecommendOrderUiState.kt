package woowacourse.shopping.presentation.cart.recommend

import woowacourse.shopping.presentation.cart.CartProductUi

data class RecommendOrderUiState(
    private val orderedProducts: List<CartProductUi> = emptyList(),
    val recommendProducts: List<CartProductUi> = emptyList(),
) {
    private val totalProducts
        get() = orderedProducts + recommendProducts

    val totalOrderIds
        get() = totalProducts.map { it.product.id }
    val totalCount
        get() = totalProducts.sumOf { it.count }

    val totalPrice
        get() =
            totalProducts
                .sumOf { it.totalPrice }

    fun increaseProductCount(
        productId: Long,
        amount: Int,
    ): RecommendOrderUiState =
        copy(
            recommendProducts =
                recommendProducts.map {
                    if (it.product.id == productId) {
                        it.copy(count = it.count + amount)
                    } else {
                        it
                    }
                },
        )

    fun decreaseProductCount(
        productId: Long,
        amount: Int,
    ): RecommendOrderUiState {
        val newProducts =
            recommendProducts.map {
                if (it.product.id == productId) {
                    it.copy(count = it.count - amount)
                } else {
                    it
                }
            }
        return copy(recommendProducts = newProducts)
    }

    fun shouldDeleteFromCart(productId: Long): Boolean {
        val product = findProduct(productId) ?: return false
        return product.count <= 1
    }

    fun findProduct(productId: Long): CartProductUi? = recommendProducts.find { it.product.id == productId }
}
