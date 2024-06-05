package woowacourse.shopping.presentation.cart

import woowacourse.shopping.domain.entity.Cart

data class CartUiState(
    val cart: Cart = Cart(),
    val pagingProducts: Map<Int, List<CartProductUi>> = emptyMap(),
    val isLoading: Boolean = false,
    val currentPage: Int = 1,
    val canLoadPrevPage: Boolean = false,
    val canLoadNextPage: Boolean = false,
) {
    val totalProducts get(): List<CartProductUi> = pagingProducts.flatMap { it.value }
    val currentPageProducts get() = pagingProducts[currentPage] ?: emptyList()
    val orderedProducts: List<CartProductUi> get() = totalProducts.filter { it.isSelected }
    val orderedProductCount get() = orderedProducts.sumOf { it.count }
    val isTotalProductsOrdered get() = (orderedProducts.size == totalProducts.size)
    val orderPrice: Int get() = orderedProducts.sumOf { it.totalPrice }
}
