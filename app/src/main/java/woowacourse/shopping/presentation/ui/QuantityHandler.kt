package woowacourse.shopping.presentation.ui

interface QuantityHandler {
    fun onDecreaseQuantity(productId: Long)

    fun onIncreaseQuantity(productId: Long)
}
