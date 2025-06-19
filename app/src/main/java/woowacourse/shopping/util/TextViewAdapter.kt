package woowacourse.shopping.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.FixedCoupon
import woowacourse.shopping.domain.model.FreeshippingCoupon
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.text.format

@BindingAdapter("expirationDate")
fun TextView.setExpirationDate(date: LocalDate?) {
    if (date == null) {
        text = ""
        return
    }

    val context = this.context
    val pattern = context.getString(R.string.date_format_full)
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val formattedDate = date.format(formatter)

    val expirationDate = context.getString(R.string.text_expiration_date_day, formattedDate)
    text = expirationDate
}

@BindingAdapter("priceText")
fun TextView.setPriceText(price: Int?) {
    text =
        if (price != null) {
            val pattern = context.getString(R.string.coupon_price)
            pattern.format(price)
        } else {
            ""
        }
}

@BindingAdapter("minimumOrderPriceText")
fun TextView.minimumOrderPriceText(coupon: Coupon?) {
    if (coupon == null) {
        this.visibility = View.GONE
        return
    }
    val formatter = this.context.getString(R.string.coupon_minimum_price)
    val text =
        when (coupon) {
            is FixedCoupon -> formatter.format(coupon.minimumAmount)
            is FreeshippingCoupon -> formatter.format(coupon.minimumAmount)
            else -> {
                this.visibility = View.GONE
                null
            }
        }

    if (text != null) {
        this.text = text
        this.visibility = View.VISIBLE
    }
}
