package woowacourse.shopping.presentation.shopping.product

import android.util.Log
import woowacourse.shopping.presentation.shopping.detail.ProductUi

data class ProductListUiState(
    val currentPage: Int = 1,
    val isLoading: Boolean = false,
    val totalProducts: List<ShoppingUiModel> = emptyList(),
    val recentProducts: List<ProductUi> = emptyList(),
) {
    val products: List<ShoppingUiModel.Product>
        get() = totalProducts.filterIsInstance<ShoppingUiModel.Product>()

    private val loadMoreModels: List<ShoppingUiModel.LoadMore>
        get() = totalProducts.filterIsInstance<ShoppingUiModel.LoadMore>()

    fun addProducts(
        newProducts: List<ShoppingUiModel.Product>,
        loadMore: List<ShoppingUiModel.LoadMore>,
    ): ProductListUiState {
        return copy(
            currentPage = currentPage + 1,
            totalProducts = products + newProducts + loadMore,
        )
    }

    fun updateProducts(newCartProducts: List<ShoppingUiModel.Product>): ProductListUiState {
        Log.d("alsong", "$newCartProducts")
        return copy(
            totalProducts =
                products.map { originalProduct ->
                    val newProduct =
                        newCartProducts.find { newProduct -> newProduct.id == originalProduct.id }
                            ?: return@map originalProduct.copy(count = 0)
                    originalProduct.copy(count = newProduct.count)
                } + loadMoreModels,
        )
    }

    fun increaseProductCount(
        productId: Long,
        amount: Int,
    ): ProductListUiState =
        copy(
            totalProducts =
                totalProducts.map {
                    if (it is ShoppingUiModel.Product && it.id == productId) {
                        it.copy(count = it.count + amount)
                    } else {
                        it
                    }
                },
        )

    fun decreaseProductCount(
        productId: Long,
        amount: Int,
    ): ProductListUiState {
        val newProducts =
            totalProducts.map {
                if (it is ShoppingUiModel.Product && it.id == productId) {
                    it.copy(count = it.count - amount)
                } else {
                    it
                }
            }
        return copy(totalProducts = newProducts)
    }

    fun shouldDeleteFromCart(productId: Long): Boolean {
        val product = findProduct(productId) ?: return false
        return product.count <= 1
    }

    fun findProduct(productId: Long): ShoppingUiModel.Product? =
        totalProducts.filterIsInstance<ShoppingUiModel.Product>().find { it.id == productId }
}
