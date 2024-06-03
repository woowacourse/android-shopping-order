package woowacourse.shopping.view.home.listener

interface QuantityClickListener {
    fun onQuantityPlusButtonClick(productId: Int)

    fun onQuantityMinusButtonClick(productId: Int)
}
