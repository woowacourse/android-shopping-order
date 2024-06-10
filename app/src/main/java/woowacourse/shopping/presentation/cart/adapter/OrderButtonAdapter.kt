package woowacourse.shopping.presentation.cart.adapter

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("setEnable")
fun View.setEnable(isEnable: Boolean) {
    this.isEnabled = isEnable
}
