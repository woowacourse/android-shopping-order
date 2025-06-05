package woowacourse.shopping.view.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@BindingAdapter("expirationDate")
fun TextView.setExpirationDate(date: LocalDate) {
    val context = this.context
    val pattern = context.getString(R.string.date_format_full)
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val formattedDate = date.format(formatter)

    val expirationDate = context.getString(R.string.text_expiration_date_day, formattedDate)
    text = expirationDate
}

@BindingAdapter("priceText")
fun TextView.setPriceText(price: Int) {
    val pattern = context.getString(R.string.thousand_unit_format)
    text = pattern.format(price)
}
