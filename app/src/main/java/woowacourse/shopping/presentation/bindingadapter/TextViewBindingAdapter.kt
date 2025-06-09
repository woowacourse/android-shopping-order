package woowacourse.shopping.presentation.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.presentation.model.AvailableTimeUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@BindingAdapter("price", "count")
fun setFormattedPrice(
    view: TextView,
    price: Int,
    count: Int,
) {
    val total = price * count
    val context = view.context
    view.text = context.getString(R.string.product_detail_price, total)
}

@BindingAdapter("availableTime")
fun setAvailableTime(
    view: TextView,
    timeRange: AvailableTimeUiModel?,
) {
    val context = view.context
    view.text = context.getString(R.string.item_coupon_available_time, timeRange)
}

@BindingAdapter("expirationDate")
fun setExpirationDate(
    view: TextView,
    date: LocalDate?,
) {
    val context = view.context
    val formattedDate = date?.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
    view.text = context.getString(R.string.item_coupon_expiration_date, formattedDate)
}
