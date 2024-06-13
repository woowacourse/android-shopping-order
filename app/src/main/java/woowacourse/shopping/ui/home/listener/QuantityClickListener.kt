package woowacourse.shopping.ui.home.listener

interface QuantityClickListener {
    fun onQuantityPlusButtonClick(productId: Int)

    fun onQuantityMinusButtonClick(productId: Int)
}
