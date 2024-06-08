package woowacourse.shopping.ui.payment.listener

interface PaymentClickListener {
    fun onBackButtonClick()

    fun onCouponClick(couponId: Int)

    fun onPaymentButtonClick()
}
