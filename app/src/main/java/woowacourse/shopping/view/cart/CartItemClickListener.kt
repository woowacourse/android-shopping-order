package woowacourse.shopping.view.cart

interface CartItemClickListener {
    fun onCartItemClick(productId: Int)

    fun onDeleteButtonClick(itemId: Int)

    fun onBackButtonClick()
}
