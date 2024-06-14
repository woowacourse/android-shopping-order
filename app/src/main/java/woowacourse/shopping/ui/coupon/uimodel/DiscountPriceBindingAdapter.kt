package woowacourse.shopping.ui.coupon.uimodel

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R

@BindingAdapter("discountPrice")
fun TextView.bindDiscountPrice(discountPrice: Int?) {
    if (discountPrice == 0) {
        text = context.getString(R.string.product_price).format(discountPrice)
    } else {
        text = context.getString(R.string.coupon_discount_price).format(discountPrice)
    }
}
