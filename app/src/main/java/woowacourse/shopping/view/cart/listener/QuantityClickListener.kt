package woowacourse.shopping.view.cart.listener

interface QuantityClickListener {
    fun onQuantityPlusButtonClick(productId: Int)

    fun onQuantityMinusButtonClick(productId: Int)
}
