package woowacourse.shopping.util.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
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
