package woowacourse.shopping.ui.cart.adapter

import android.widget.CheckBox
import androidx.databinding.BindingAdapter
import woowacourse.shopping.ui.cart.CartListener

@BindingAdapter("onChecked")
fun CheckBox.onChecked(listener: CartListener) {
    setOnCheckedChangeListener { _, isChecked ->
        listener.selectAllCartItem(isChecked)
    }
}
