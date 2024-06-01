package woowacourse.shopping.presentation.cart

data class CartUiState(
    val isLoading: Boolean = false,
    val pagingProducts: Map<Int, List<CartProductUi>> = emptyMap(),
    val currentPage: Int = 1,
    val canLoadPrevPage: Boolean = false,
    val canLoadNextPage: Boolean = false,
) {
    private val totalProducts get(): List<CartProductUi> = pagingProducts.flatMap { it.value }

    val currentPageProducts get() = pagingProducts[currentPage] ?: emptyList()
    val orderedProducts: List<CartProductUi> get() = totalProducts.filter { it.isSelected }
    val orderedProductCount get() = orderedProducts.sumOf { it.count }
    val isTotalProductsOrdered get() = (orderedProducts.size == totalProducts.size)
    val orderPrice: Int get() = orderedProducts.sumOf { it.totalPrice }
}
