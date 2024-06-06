package woowacourse.shopping.presentation.ui.payment.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

@BindingAdapter("orderTotalPrice")
fun TextView.setOrderTotalPrice(paymentUiModel: PaymentUiModel?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.orderPrice)
}
@BindingAdapter("disCountTotalPrice")
fun TextView.setDiscountTotalPrice(paymentUiModel: PaymentUiModel?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.discountPrice)
}

@BindingAdapter("deliveryTotalPrice")
fun TextView.setDeliveryTotalPrice(paymentUiModel: PaymentUiModel?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.totalDeliveryPrice)
}
@BindingAdapter("paymentTotalPrice")
fun TextView.setPaymentTotalPrice(paymentUiModel: PaymentUiModel?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.totalPrice)
}