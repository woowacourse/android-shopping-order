package woowacourse.shopping.ui.payment.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.ui.state.UiState
import java.time.LocalDate

@BindingAdapter("app:discountPrice")
fun setDiscountPrice(
    view: TextView,
    price: Int,
) {
    view.text = view.context.getString(R.string.discount_price_format, price)
}

@BindingAdapter("app:expirationDate")
fun setExpirationDate(
    view: TextView,
    expirationDate: LocalDate,
) {
    view.text = view.context.getString(
        R.string.expiration_date_format,
        expirationDate.year,
        expirationDate.monthValue,
        expirationDate.dayOfMonth
    )
}

@BindingAdapter("app:minOrderPrice")
fun setMinOrderPrice(
    view: TextView,
    price: Int,
) {
    view.text = view.context.getString(R.string.min_order_price_format, price)
}

@BindingAdapter("app:isCouponEmpty", "app:paymentUiState", requireAll = true)
fun <T> setEmptyCouponVisibility(
    textView: TextView,
    isCouponEmpty: Boolean,
    paymentUiState: UiState<T>,
) {
    textView.visibility =
        if (paymentUiState is UiState.Success && isCouponEmpty) View.VISIBLE else View.GONE
}

@BindingAdapter("app:isCouponEmpty", "app:paymentUiState", requireAll = true)
fun <T> setCouponVisibility(
    recyclerView: RecyclerView,
    isCouponEmpty: Boolean,
    paymentUiState: UiState<T>,
) {
    recyclerView.visibility =
        if ((paymentUiState is UiState.Success && !isCouponEmpty) || paymentUiState is UiState.Loading) View.VISIBLE else View.GONE
}
