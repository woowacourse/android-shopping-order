package woowacourse.shopping.presentation.ui

interface QuantityHandler {
    fun decreaseQuantity(productId: Long)

    fun increaseQuantity(productId: Long)
}
