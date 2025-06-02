package woowacourse.shopping.view.common

interface ProductQuantityClickListener {
    fun onPlusShoppingCartClick(quantityTarget: QuantityTarget)

    fun onMinusShoppingCartClick(quantityTarget: QuantityTarget)
}
