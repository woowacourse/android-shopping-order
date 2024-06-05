package woowacourse.shopping.presentation.ui.cart.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel

@BindingAdapter("cartImgUrl")
fun ImageView.setCartImgUrl(url: String?) {
    Glide.with(this.context)
        .load(url)
        .into(this)
}

@BindingAdapter("cartOrderCount")
fun TextView.setCartOrderCount(uiState: UiState<List<CartProductUiModel>>?) {
    if (uiState is UiState.Success) {
        this.text =
            uiState.data.filter {
                it.isChecked
            }.sumOf { it.cartProduct.quantity }.toString()
    }
}

@BindingAdapter("cartPrice")
fun TextView.setCartPrice(uiState: UiState<List<CartProductUiModel>>?) {
    if (uiState is UiState.Success) {
        this.text =
            this.context.getString(
                R.string.won,
                uiState.data.sumOf {
                    it.cartProduct.quantity * it.cartProduct.price
                },
            )
    }
}
