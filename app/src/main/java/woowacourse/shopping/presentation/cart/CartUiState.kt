package woowacourse.shopping.presentation.cart

data class CartUiState(
    val isLoading: Boolean = false,
    private val pagingProducts: Map<Int, List<CartProductUi>> = emptyMap(),
    val currentPage: Int = 1,
    val canLoadPrevPage: Boolean = false,
    val canLoadNextPage: Boolean = false,
) {
    val totalProducts get(): List<CartProductUi> = pagingProducts.flatMap { it.value }

    val totalProductCount get() = totalProducts.sumOf { it.count }

    val currentPageProducts get() = pagingProducts[currentPage] ?: emptyList()
    val orderedProducts: List<CartProductUi> get() = totalProducts.filter { it.isSelected }

    val isTotalProductsOrdered get() = (orderedProducts.size == totalProducts.size)

    val orderPrice: Int get() = orderedProducts.sumOf { it.totalPrice }

    fun toggleProductSelected(productId: Long): CartUiState {
        val newCurrentProducts =
            currentPageProducts.map {
                if (it.product.id == productId) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it
                }
            }
        val newPagingProducts = pagingProducts + (currentPage to newCurrentProducts)
        return copy(
            pagingProducts = newPagingProducts,
        )
    }

    fun toggleTotalOrderProducts(): CartUiState {
        val isSelectedAll = isTotalProductsOrdered
        val newPagingProducts =
            pagingProducts.map {
                it.key to
                    it.value.map { cartProductUi ->
                        cartProductUi.copy(isSelected = !isSelectedAll)
                    }
            }.toMap()
        return copy(
            pagingProducts = newPagingProducts,
        )
    }

    fun updateTotalProducts(products: List<CartProductUi>): CartUiState {
        val newPagingProducts =
            products.chunked(5).mapIndexed { index, chunkedProducts ->
                (index + 1) to chunkedProducts
            }.toMap()
        return copy(
            pagingProducts = newPagingProducts,
        )
    }

    fun updateTargetPageProducts(
        products: List<CartProductUi>,
        targetPage: Int,
    ): CartUiState {
        val originalCurrentPageProducts = pagingProducts[targetPage] ?: products
        val ids = originalCurrentPageProducts.map { it.product.id }
        val newProducts =
            products.map { cartProduct ->
                val newId = cartProduct.product.id
                if (newId in ids) {
                    val original = originalCurrentPageProducts.find { it.product.id == newId }
                    return@map original ?: cartProduct
                }
                cartProduct
            }

        val newPagingProducts = pagingProducts + (targetPage to newProducts)
        return copy(
            currentPage = targetPage,
            pagingProducts = newPagingProducts,
        )
    }

    fun increaseProductCount(
        productId: Long,
        amount: Int,
    ): CartUiState {
        val newCurrentProducts =
            currentPageProducts.map {
                if (it.product.id == productId) {
                    it.copy(count = it.count + amount)
                } else {
                    it
                }
            }
        val newPagingProducts = pagingProducts + (currentPage to newCurrentProducts)
        return copy(
            pagingProducts = newPagingProducts,
        )
    }

    fun decreaseProductCount(
        productId: Long,
        amount: Int,
    ): CartUiState {
        val newCurrentProducts =
            currentPageProducts.map {
                if (it.product.id == productId) {
                    it.copy(count = it.count - amount)
                } else {
                    it
                }
            }
        val newPagingProducts = pagingProducts + (currentPage to newCurrentProducts)
        return copy(
            pagingProducts = newPagingProducts,
        )
    }

    fun canDecreaseProductCount(
        productId: Long,
        countLimit: Int,
    ): Boolean {
        val product = findProductAtCurrentPage(productId) ?: return false
        return product.count > countLimit
    }

    fun findProductAtCurrentPage(productId: Long): CartProductUi? = currentPageProducts.find { it.product.id == productId }
}
