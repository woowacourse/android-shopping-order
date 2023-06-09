package woowacourse.shopping.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R

@BindingAdapter("setPrice")
fun setPriceView(textView: TextView, price: Int?) {
    textView.text = textView.context.getString(R.string.price_format, price ?: 0)
}
