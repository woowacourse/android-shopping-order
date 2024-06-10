package woowacourse.shopping.presentation.ui.payment.adapter

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiState

@BindingAdapter("orderTotalPrice")
fun TextView.setOrderTotalPrice(paymentUiModel: PaymentUiState?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.orderPrice)
}

@BindingAdapter("disCountTotalPrice")
fun TextView.setDiscountTotalPrice(paymentUiModel: PaymentUiState?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.priceDiscount)
}

@BindingAdapter("deliveryTotalPrice")
fun TextView.setDeliveryTotalPrice(paymentUiModel: PaymentUiState?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.totalDeliveryPrice)
}

@BindingAdapter("paymentTotalPrice")
fun TextView.setPaymentTotalPrice(paymentUiModel: PaymentUiState?) {
    this.text = this.context.getString(R.string.won, paymentUiModel?.totalPrice)
}

@BindingAdapter("couponValidation")
fun TextView.setCouponValidation(expirationDate: String?) {
    if (expirationDate != null) {
        this.isVisible = true
        this.text = this.context.getString(R.string.validation_title, expirationDate)
    } else {
        this.isVisible = false
    }
}

@BindingAdapter("couponMinimumAmount")
fun TextView.setCouponMinimumAmount(minimumAmount: Int?) {
    if (minimumAmount != null) {
        this.isVisible = true
        this.text = this.context.getString(R.string.minimum_amount, minimumAmount)
    } else {
        this.isVisible = false
    }
}
