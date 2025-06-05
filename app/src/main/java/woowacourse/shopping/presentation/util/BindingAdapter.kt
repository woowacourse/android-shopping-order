package woowacourse.shopping.presentation.util

import android.widget.CheckBox
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.view.cart.cartItem.CartItemEventHandler

@BindingAdapter("imageUrl")
fun setImageBindingAdapter(
    imageView: ImageView,
    imageUrl: String?,
) {
    Glide
        .with(imageView)
        .load(imageUrl)
        .into(imageView)
}

@BindingAdapter("isChecked", "onCheckedChanged", "cartItem")
fun setOnCheckedChangedBindingAdapter(
    checkBox: CheckBox,
    isChecked: Boolean,
    eventHandler: CartItemEventHandler,
    cartItem: CartItemUiModel,
) {
    checkBox.setOnCheckedChangeListener(null)
    checkBox.isChecked = isChecked
    checkBox.setOnCheckedChangeListener { _, checked ->
        eventHandler.onProductSelectionToggle(cartItem, checked)
    }
}
