package woowacourse.shopping.view.common

interface ProductQuantityClickListener {
    fun onPlusShoppingCartClick(quantityObservable: QuantityObservable)

    fun onMinusShoppingCartClick(quantityObservable: QuantityObservable)
}
