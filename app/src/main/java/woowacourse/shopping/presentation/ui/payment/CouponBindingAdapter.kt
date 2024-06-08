package woowacourse.shopping.presentation.ui.payment

import android.icu.text.SimpleDateFormat
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.Locale


@BindingAdapter("expirationDate")
fun TextView.setFormattedExpirationDate(date: String) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    val parsedDate = inputFormat.parse(date)
    if (parsedDate != null) {
        val formattedDate = outputFormat.format(parsedDate)
        text = "만료일: $formattedDate"
    }
}

@BindingAdapter("minimumAmountIsVisible")
fun TextView.setMinimumAmount(minimumAmount: Int) {
    if(minimumAmount > 0) {
        text = "최소 주문 금액: ${minimumAmount}원"
    }
}
