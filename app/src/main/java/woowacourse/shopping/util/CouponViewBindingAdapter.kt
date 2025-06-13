package woowacourse.shopping.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@BindingAdapter("app:formattedExpirationDateText")
fun setExpirationDateText(
    textView: TextView,
    rawDate: String?,
) {
    if (rawDate.isNullOrBlank()) {
        textView.text = ""
        return
    }

    runCatching {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
        val date = LocalDate.parse(rawDate, inputFormat)
        val formattedDate = date.format(outputFormat)
        textView.text = textView.context.getString(R.string.expiration_date, formattedDate)
    }.onFailure {
        textView.text = ""
    }
}

@BindingAdapter("app:couponMinimumAmountVisibility")
fun setCouponMinimumAmount(
    textView: TextView,
    couponMinimumAmount: Int?,
) {
    if (couponMinimumAmount == null) {
        textView.visibility = View.GONE
    } else {
        textView.text = textView.context.getString(R.string.minimum_amount, couponMinimumAmount)
    }
}
