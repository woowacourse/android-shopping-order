package woowacourse.shopping.view.cart

interface OnClickNavigateShoppingCart {
    fun clickBack()

    fun clickOrder()

    fun clickCartItem(productId: Long)
}
