package woowacourse.shopping.presentation.util

import android.widget.CheckBox
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.view.cart.cartitem.CartItemEventHandler

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

@BindingAdapter(value = ["cartItem", "eventHandler"], requireAll = true)
fun setOnCheckedChangedBindingAdapter(
    checkBox: CheckBox,
    cartItem: CartItemUiModel,
    eventHandler: CartItemEventHandler,
) {
    checkBox.setOnCheckedChangeListener(null)
    checkBox.isChecked = cartItem.isSelected
    checkBox.setOnCheckedChangeListener { _, _ ->
        eventHandler.onProductSelectionToggle(cartItem)
    }
}
