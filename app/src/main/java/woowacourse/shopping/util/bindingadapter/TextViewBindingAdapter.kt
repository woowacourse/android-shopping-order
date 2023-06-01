package woowacourse.shopping.util.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.model.PointModel
import woowacourse.shopping.model.UiPrice
import woowacourse.shopping.model.UiProductCount

@BindingAdapter("bind:price")
fun TextView.setPrice(price: UiPrice?) {
    text = context.getString(R.string.price_format, price?.value ?: 0)
}

@BindingAdapter("bind:quantity")
fun TextView.setQuantity(quantity: UiProductCount) {
    text = context.getString(R.string.quantity_format, quantity.value)
}

@BindingAdapter("bind:point")
fun TextView.setPoint(point: PointModel) {
    text = context.getString(R.string.point_format, point.value)
}

@BindingAdapter(
    "bind:productName",
    "bind:orderProductSize",
    requireAll = true
)
fun TextView.setOrderProductNameWithSize(
    productName: String,
    orderProductSize: Int,
) {
    text = if (orderProductSize > 1) {
        context.getString(
            R.string.order_product_name_format_with_quantity,
            productName,
            orderProductSize - 1
        )
    } else {
        productName
    }
}
