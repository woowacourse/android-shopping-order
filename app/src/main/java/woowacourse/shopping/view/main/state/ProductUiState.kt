package woowacourse.shopping.view.main.state

data class ProductUiState(
    val productItems: List<ProductState> = emptyList(),
    val historyItems: List<HistoryState> = emptyList(),
    val load: LoadState = LoadState.CannotLoad,
) {
    val lastSeenProductId
        get() = historyItems.firstOrNull()?.productId

    val productIds
        get() = productItems.map { it.item.id }

    val sumOfCartQuantity
        get() = productItems.sumOf { it.cartQuantity.value }

    fun modifyUiState(newState: ProductState): ProductUiState {
        val targetIndex = targetIndex(newState.item.id)
        val mutableItems = productItems.toMutableList()
        mutableItems[targetIndex] = newState

        return copy(productItems = mutableItems)
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
