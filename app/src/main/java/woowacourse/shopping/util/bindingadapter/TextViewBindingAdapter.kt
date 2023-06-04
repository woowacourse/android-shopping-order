package woowacourse.shopping.util.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.model.UiOrderedProduct
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

@BindingAdapter("bind:order_id")
fun TextView.setOrderId(orderId: Int?) {
    text = context.getString(R.string.order_id_text, orderId)
}

@BindingAdapter("bind:history_names")
fun TextView.setNames(orderedProducts: List<UiOrderedProduct>?) {
    val names = mutableListOf<String>()
    orderedProducts?.forEach { names.add(it.name) }
    val namesString = names.joinToString(", ")
    text = namesString
}

@BindingAdapter("bind:history_price")
fun TextView.setPrice(price: Int?) {
    text = context.getString(R.string.price_format, price)
}

@BindingAdapter("bind:history_count")
fun TextView.setCount(orderedProducts: List<UiOrderedProduct>?) {
    val totalCount = orderedProducts?.sumOf { it.quantity }
    text = context.getString(R.string.order_count_text, totalCount)
}

fun <T> Iterable<T>.sumOf(selector: (T) -> Int): Int {
    var sum = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
