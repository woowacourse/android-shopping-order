package woowacourse.shopping.ui.cart.adapter

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CheckBox
import androidx.databinding.BindingAdapter
import woowacourse.shopping.ui.cart.CartListener

@BindingAdapter("onChecked")
fun CheckBox.onChecked(listener: CartListener) {
    setOnClickListener {
        listener.selectAllCartItem(isChecked)
    }
}

@BindingAdapter("setVisibility")
fun View.setVisibility(isVisible: Boolean) {
    if (isVisible) {
        visibility = VISIBLE
    } else {
        visibility = GONE
    }
}
