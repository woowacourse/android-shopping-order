package woowacourse.shopping.view.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import java.time.LocalDate

@BindingAdapter("imageUrl")
fun setImageUrl(
    view: ImageView,
    imageUrl: String?,
) {
    Glide
        .with(view.context)
        .load(imageUrl)
        .into(view)
}

@BindingAdapter("expirationDate")
fun setExpirationDate(
    view: TextView,
    expirationDate: LocalDate,
) {
    view.text =
        view.context.getString(
            R.string.couponExpirationDate,
            expirationDate.year,
            expirationDate.monthValue,
            expirationDate.dayOfMonth,
        )
}

@BindingAdapter("discount")
fun setDiscount(
    view: TextView,
    discount: Int,
) {
    if (discount > 0) {
        view.text = view.context.getString(R.string.product_discount_format, discount)
    } else {
        view.text = view.context.getString(R.string.product_price_format, discount)
    }
}
