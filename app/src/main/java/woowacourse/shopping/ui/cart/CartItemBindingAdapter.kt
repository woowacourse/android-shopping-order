package woowacourse.shopping.ui.cart

import android.widget.CheckBox
import androidx.databinding.BindingAdapter

@BindingAdapter("onChecked")
fun CheckBox.onChecked(listener: CartListener) {
    setOnClickListener {
        listener.toggleAllCartItem(isChecked)
    }
}
