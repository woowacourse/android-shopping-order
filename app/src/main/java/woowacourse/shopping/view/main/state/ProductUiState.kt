package woowacourse.shopping.view.main.state

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCart

data class ProductUiState(
    val productItems: List<ProductState> = emptyList(),
    val historyItems: List<HistoryState> = emptyList(),
    val load: LoadState = LoadState.CannotLoad,
) {
    val productItemSize: Int
        get() = productItems.size

    val lastSeenProductId
        get() = historyItems.firstOrNull()?.productId

    val lastSeenProductCategory
        get() = historyItems.firstOrNull()?.category

    val sumOfCartQuantity
        get() = productItems.sumOf { it.cartQuantity.value }

    fun modifyUiState(newState: ProductState): ProductUiState {
        val targetIndex = targetIndex(newState.item.id)
        val frontItems = productItems.subList(0, targetIndex)
        val backItems = productItems.subList(targetIndex + 1, productItems.size)
        return copy(productItems = frontItems + newState + backItems)
    }

    fun modifyQuantity(carts: List<ShoppingCart>): ProductUiState {
        val result =
            productItems.map { product ->
                val productId = product.productId

                carts.find { it.productId == productId }?.let { cart ->
                    product.modifyQuantity(cart.quantity)
                } ?: run {
                    product.copy(cartQuantity = Quantity(0))
                }
            }

        return copy(productItems = result)
    }

    fun increaseCartQuantity(productId: Long): ProductState {
        val targetIndex = targetIndex(productId)
        val target = productItems[targetIndex]
        val result = target.increaseCartQuantity()

        return result
    }

    fun decreaseCartQuantity(productId: Long): ProductState {
        val targetIndex = targetIndex(productId)
        val result = productItems[targetIndex].decreaseCartQuantity()

        return result
    }

    private fun targetIndex(productId: Long) = productItems.indexOfFirst { it.item.id == productId }
}
