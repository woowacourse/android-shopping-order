package woowacourse.shopping.presentation.ui.order

interface OrderHandler {
    fun onCheckBoxClicked(couponId: Long)

    fun onPayButtonClicked()
}
