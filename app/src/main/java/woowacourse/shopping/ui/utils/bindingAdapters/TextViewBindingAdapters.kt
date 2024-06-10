package woowacourse.shopping.ui.utils.bindingAdapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@BindingAdapter("expirationDate")
fun TextView.bindExpirationDate(date: LocalDate) {
    this.text =
        date.format(DateTimeFormatter.ofPattern(context.getString(R.string.coupon_expiration_date)))
}
