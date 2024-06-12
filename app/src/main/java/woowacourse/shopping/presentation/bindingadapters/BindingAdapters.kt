package woowacourse.shopping.presentation.bindingadapters

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.coupon.Bogo
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.model.coupon.Fixed5000
import woowacourse.shopping.domain.model.coupon.FreeShipping
import woowacourse.shopping.domain.model.coupon.MiracleSale
import woowacourse.shopping.presentation.event.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter("app:imageUrl")
fun loadImage(
    view: ImageView,
    url: String?,
) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(url)
            .into(view)
    }
}

@BindingAdapter("app:loadingVisibility")
fun setLoadingVisibility(
    view: View,
    isLoading: Event<Boolean>,
) {
    val isVisible = isLoading.getContentIfNotHandled()
    if (isVisible != null) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}

@BindingAdapter("app:price")
fun setPrice(
    view: TextView,
    price: Long?,
) {
    view.text = price?.let { view.context.getString(R.string.price_format, it) } ?: ""
}

@BindingAdapter("app:visibility")
fun setLoadMoreBtnVisibility(
    view: View,
    isVisible: Boolean?,
) {
    if (isVisible == true) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("app:shoppingCounterVisibility")
fun setVisibility(
    view: View,
    isVisible: Boolean?,
) {
    if (isVisible == true) {
        if (view is ImageView) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    } else {
        if (view is ImageView) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("app:cartViewVisibility")
fun setViewVisibility(
    view: View,
    isVisible: Boolean?,
) {
    if (isVisible == true) {
        if (view is TextView) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    } else {
        if (view is TextView) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("app:orderWithQuantityText")
fun setOrderWithQuantityText(
    view: TextView,
    totalQuantity: Int?,
) {
    view.text =
        view.context.getString(R.string.make_order_with_total_quantity, totalQuantity ?: 0)
}

@BindingAdapter("app:couponExpirationDate")
fun TextView.bindCouponExpirationDate(expirationDate: Date?) {
    val date = expirationDate ?: return
    val pattern = "만료일: yyyy년 MM월 dd일"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    val formattedExpirationDate = formatter.format(date)
    this.text = formattedExpirationDate
}

@SuppressLint("StringFormatMatches")
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
                    context.getString(R.string.price_format, minimumAmount),
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
