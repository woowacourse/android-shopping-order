package woowacourse.shopping.presentation.ui.order

interface OrderHandler {
    fun selectCoupon(couponId: Long)

    fun completeOrder()
}
