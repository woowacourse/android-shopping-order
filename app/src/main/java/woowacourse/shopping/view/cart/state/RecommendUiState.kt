package woowacourse.shopping.view.cart.state

import woowacourse.shopping.view.main.state.ProductState

data class RecommendUiState(val item: List<ProductState>) {
    fun modifyUiState(newState: ProductState): RecommendUiState {
        val targetIndex = targetIndex(newState.item.id)
        val frontItems = item.subList(0, targetIndex)
        val backItems = item.subList(targetIndex + 1, item.size)
        return copy(item = frontItems + newState + backItems)
    }

    fun increaseQuantity(productId: Long): ProductState {
        val targetIndex = targetIndex(productId)
        val target = item[targetIndex]
        val result = target.increaseCartQuantity()
        return result
    }

    fun decreaseCartQuantity(productId: Long): ProductState {
        val targetIndex = targetIndex(productId)
        val result = item[targetIndex].decreaseCartQuantity()
        return result
    }

    private fun targetIndex(productId: Long) = item.indexOfFirst { it.item.id == productId }
}
