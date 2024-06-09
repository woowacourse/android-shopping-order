package woowacourse.shopping.view.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.view.order.CouponViewItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@BindingAdapter("app:imageUrl")
fun loadImage(
    view: ImageView,
    url: String?,
) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(view)
    }
}

@BindingAdapter("app:price")
fun setPrice(
    view: TextView,
    price: Int,
) {
    view.text = view.context.getString(R.string.price_format, price)
}

@BindingAdapter("app:viewVisibility")
fun setViewVisibility(
    view: View,
    isVisible: Boolean,
) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("shopping:expirationDate")
fun setExpirationDate(
    textView: TextView,
    expirationDate: LocalDate?,
) {
    val context = textView.context
    if (expirationDate == null) return
    val formatter = DateTimeFormatter.ofPattern(context.getString(R.string.date_format))
    textView.text =
        context.getString(R.string.order_expiration_date, expirationDate.format(formatter))
}

@BindingAdapter("shopping:minimumExpense")
fun setExpirationDate(
    textView: TextView,
    couponViewItem: CouponViewItem?,
) {
    val context = textView.context
    if (couponViewItem == null) return
    if (couponViewItem !is CouponViewItem.CouponItem) {
        textView.visibility = View.INVISIBLE
        return
    }
    val minimumPrice =
        when (couponViewItem.coupon) {
            is Coupon.Fixed ->
                context.getString(
                    R.string.price_format,
                    couponViewItem.coupon.minimumAmount,
                )

            is Coupon.FreeShipping ->
                context.getString(
                    R.string.price_format,
                    couponViewItem.coupon.minimumAmount,
                )

            else -> return
        }
    textView.text = context.getString(R.string.order_minimum_price, minimumPrice)
}
