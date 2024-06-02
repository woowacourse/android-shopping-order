package woowacourse.shopping.presentation.common

import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Cart

@BindingAdapter("setOrderButtonColor")
fun AppCompatButton.setOrderButtonColor(carts: List<Cart>) {
    val isOrderEnabled = carts.count { it.isChecked } > 0

    background = if (isOrderEnabled) ContextCompat.getDrawable(context, R.color.green)
    else ContextCompat.getDrawable(context, R.color.gray6)
}

@BindingAdapter("setOrderButtonEnabled")
fun AppCompatButton.setOrderButtonEnabled(carts: List<Cart>) {
    val isOrderEnabled = carts.count { it.isChecked } > 0

    isEnabled = isOrderEnabled
}
