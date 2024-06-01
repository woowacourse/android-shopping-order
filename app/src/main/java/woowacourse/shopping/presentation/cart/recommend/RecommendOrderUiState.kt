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
}
