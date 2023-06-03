package woowacourse.shopping.util.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.model.UiPrice

@BindingAdapter("bind:price")
fun TextView.setPrice(price: UiPrice?) {
    text = context.getString(R.string.price_format, price?.value)
}

@BindingAdapter("bind:point")
fun TextView.setPoint(point: Point?) {
    text = context.getString(R.string.point_format, point?.availablePoint)
}

@BindingAdapter("bind:count")
fun TextView.setCount(count: Int?) {
    text = context.getString(R.string.count_text, count)
}

@BindingAdapter("bind:discount")
fun TextView.setDiscount(discount: Int?) {
    text = context.getString(R.string.discount_text, discount)
}
