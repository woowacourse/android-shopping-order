package woowacourse.shopping.view.cart

interface QuantityClickListener {
    fun onQuantityPlusButtonClick(productId: Int)

    fun onQuantityMinusButtonClick(productId: Int)
}
