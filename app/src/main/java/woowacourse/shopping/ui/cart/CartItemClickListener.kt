package woowacourse.shopping.ui.cart

interface CartItemClickListener {
    fun clickCheckBox(productId: Long)

    fun removeCartItem(productId: Long)
}
