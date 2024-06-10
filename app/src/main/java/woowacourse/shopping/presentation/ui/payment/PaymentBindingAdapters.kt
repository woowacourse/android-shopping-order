package woowacourse.shopping.presentation.ui.payment

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.coupon.BogoCondition
import woowacourse.shopping.domain.model.coupon.MinimumPurchaseAmountCondition
import woowacourse.shopping.domain.model.coupon.TimeBasedCondition
import woowacourse.shopping.presentation.common.currency
import woowacourse.shopping.presentation.model.CouponUiModel
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
fun TextView.bindCouponDescription(couponUiModel: CouponUiModel) {
    val description =
        when (couponUiModel.couponCondition) {
            is BogoCondition -> {
                val buyQuantity = couponUiModel.buyQuantity ?: return
                val getQuantity = couponUiModel.getQuantity ?: return
                context.getString(R.string.bogo_coupon_description, buyQuantity, getQuantity)
            }

            is MinimumPurchaseAmountCondition -> {
                val minimumAmount = couponUiModel.minimumAmount ?: return
                context.getString(
                    R.string.fixed5000_coupon_description,
                    minimumAmount.currency(this.context),
                )
            }

            is TimeBasedCondition -> {
                val availableTime = couponUiModel.availableTime ?: return
                context.getString(
                    R.string.miracle_sale_coupon_description,
                    availableTime.start.hour,
                    availableTime.end.hour,
                )
            }
        }

    this.text = description
}
