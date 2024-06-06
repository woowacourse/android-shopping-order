package woowacourse.shopping.presentation.ui.payment

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.coupon.Bogo
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.model.coupon.Fixed5000
import woowacourse.shopping.domain.model.coupon.FreeShipping
import woowacourse.shopping.domain.model.coupon.MiracleSale
import woowacourse.shopping.presentation.common.currency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter("couponExpirationDate")
fun TextView.bindCouponExpirationDate(expirationDate: Date?) {
    val date = expirationDate ?: return
    val pattern = context.getString(R.string.coupon_expiration_date_format)
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    val formattedExpirationDate = formatter.format(date)
    this.text = formattedExpirationDate
}

@BindingAdapter("couponDescription")
fun TextView.bindCouponDescription(couponState: CouponState?) {
    val description =
        when (couponState) {
            is Bogo -> {
                val buyQuantity = couponState.coupon.buyQuantity ?: return
                val getQuantity = couponState.coupon.getQuantity ?: return
                context.getString(R.string.bogo_coupon_description, buyQuantity, getQuantity)
            }

            is Fixed5000 -> {
                val minimumAmount = couponState.coupon.minimumAmount ?: return
                context.getString(
                    R.string.fixed5000_coupon_description,
                    minimumAmount.currency(this.context),
                )
            }

            is FreeShipping -> context.getString(R.string.free_shipping_coupon_description)

            is MiracleSale -> {
                val availableTime = couponState.coupon.availableTime ?: return
                context.getString(
                    R.string.miracle_sale_coupon_description,
                    availableTime.start.hour,
                    availableTime.end.hour,
                )
            }

            else -> return
        }

    this.text = description
}
