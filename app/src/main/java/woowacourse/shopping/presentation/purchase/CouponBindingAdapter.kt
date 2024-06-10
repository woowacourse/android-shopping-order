package woowacourse.shopping.presentation.purchase

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.domain.model.Coupon
import woowacourse.shopping.R
import java.time.LocalDate

@BindingAdapter("expirationDate")
fun TextView.expirationDate(date: LocalDate) {
    val title = this.context.getString(R.string.expiration_date)
    this.text = title + date.toString()
}

@BindingAdapter("minimumPrice")
fun TextView.minimumPrice(coupon: Coupon) {
    if (coupon is Coupon.FixedCoupon) {
        val title = this.context.getString(R.string.minimum_price)
        this.text = title + coupon.minimumAmount
    }
    if (coupon is Coupon.FreeShippingCoupon) {
        val title = this.context.getString(R.string.minimum_price)
        this.text = title + coupon.minimumAmount
    }
}
