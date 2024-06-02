package woowacourse.shopping.view.cart.listener

interface CartItemClickListener {
    fun onCheckBoxClick(cartItemId: Int)

    fun onCartItemClick(productId: Int)

    fun onDeleteButtonClick(cartItemId: Int)
}
