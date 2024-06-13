package woowacourse.shopping.ui.coupon

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import java.time.LocalDate

@BindingAdapter("expirationDate")
fun TextView.setExpirationDateMessage(expirationDate: LocalDate) {
    text =
        resources.getString(
            R.string.expiration_date,
            expirationDate.year,
            expirationDate.monthValue,
            expirationDate.dayOfMonth,
        )
}
