package woowacourse.shopping.presentation.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import java.time.LocalDate
import java.time.LocalTime
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

@BindingAdapter("formattedDate")
fun TextView.setFormattedDate(date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    this.text = formatter.format(date)
}

@BindingAdapter("startTime", "endTime")
fun TextView.setFormattedTime(
    startTime: LocalTime?,
    endTime: LocalTime?,
) {
    val formatted =
        this.context.getString(
            R.string.order_available_using_time_format,
            startTime?.hour,
            endTime?.hour,
        )
    this.text = formatted
}
