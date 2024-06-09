package woowacourse.shopping.presentation.ui.shoppingcart.payment

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.coupons.BOGO
import woowacourse.shopping.domain.model.coupons.Coupon
import woowacourse.shopping.domain.model.coupons.FIXED5000
import woowacourse.shopping.domain.model.coupons.FREESHIPPING
import woowacourse.shopping.domain.model.coupons.MIRACLESALE
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@BindingAdapter("expiredDate")
fun TextView.setExpiredDate(date: LocalDate) {
    text =
        context.getString(R.string.expired_date)
            .format(date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")))
}

@BindingAdapter("minimumPayment")
fun TextView.setMinimumPayment(coupon: Coupon) {
    text =
        when (coupon) {
            is FIXED5000 -> context.getString(R.string.minimum_amount).format(coupon.minimumAmount)
            is FREESHIPPING -> context.getString(R.string.minimum_amount).format(coupon.minimumAmount)
            is BOGO, is MIRACLESALE -> ""
        }
}
