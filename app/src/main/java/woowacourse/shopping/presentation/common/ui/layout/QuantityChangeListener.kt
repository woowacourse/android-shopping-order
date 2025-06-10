package woowacourse.shopping.presentation.common.ui.layout

interface QuantityChangeListener {
    fun increaseQuantity(productId: Long)

    fun decreaseQuantity(productId: Long)
}
