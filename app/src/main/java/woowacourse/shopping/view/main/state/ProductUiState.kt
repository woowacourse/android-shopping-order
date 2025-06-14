package woowacourse.shopping.view.main.state

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.domain.product.Product

data class ProductUiState(
    val productItems: List<ProductState> = emptyList(),
    val historyItems: List<Product> = emptyList(),
    val sumOfCartQuantity: Int = 0,
    val load: LoadState = LoadState.CannotLoad,
    val isFetching: Boolean = true,
) {
    val lastSeenProductId = historyItems.firstOrNull()?.id

    val lastSeenProductCategory = historyItems.firstOrNull()?.category

    val productItemSize: Int
        get() = productItems.size

    fun modifySumOfCartQuantity(quantity: Int): ProductUiState {
        return copy(sumOfCartQuantity = quantity)
    }

    fun modifyUiState(newState: ProductState): ProductUiState {
        val targetIndex = targetIndex(newState.item.id)
        val mutableItems = productItems.toMutableList()
        mutableItems[targetIndex] = newState

        return copy(productItems = mutableItems)
    }

    fun modifyQuantity(carts: ShoppingCarts): ProductUiState {
        val result =
            productItems.map { product ->
                val productId = product.productId

                carts.shoppingCarts.find { it.productId == productId }?.let { cart ->
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

    fun fetchToggleState() = copy(isFetching = !isFetching)

    private fun targetIndex(productId: Long) = productItems.indexOfFirst { it.item.id == productId }
}
