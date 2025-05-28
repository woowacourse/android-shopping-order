package woowacourse.shopping.presentation.ui.layout

interface QuantityChangeListener {
    fun increaseQuantity(
        cartId: Long,
        quantity: Int,
    )

    fun decreaseQuantity(
        cartId: Long,
        quantity: Int,
    )
}
