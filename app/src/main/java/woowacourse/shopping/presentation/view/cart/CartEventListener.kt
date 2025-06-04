package woowacourse.shopping.presentation.view.cart

interface CartEventListener {
    fun onBatchSelect(isChecked: Boolean)

    fun onPlaceOrderClick()
}
