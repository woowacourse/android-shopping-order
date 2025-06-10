package woowacourse.shopping.presentation.bindingadapter

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleIf")
fun setVisible(
    view: View,
    condition: Boolean,
) {
    view.visibility = if (condition) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleIfNotNull")
fun View.setVisibleIfNotNull(value: Any?) {
    this.isVisible = value != null
}
