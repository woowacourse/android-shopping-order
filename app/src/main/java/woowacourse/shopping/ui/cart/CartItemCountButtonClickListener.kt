package woowacourse.shopping.ui.cart

interface CartItemCountButtonClickListener {
    fun plusCartCount(productId: Long)

    fun minusCartCount(productId: Long)
}
