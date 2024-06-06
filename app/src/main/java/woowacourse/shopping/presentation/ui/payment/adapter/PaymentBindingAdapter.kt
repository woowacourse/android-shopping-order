package woowacourse.shopping.presentation.ui.payment.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

@BindingAdapter("paymentTotalPrice")
fun TextView.setPaymentTotalPrice(paymentUiModel: PaymentUiModel?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.totalPrice)
}