package woowacourse.shopping.presentation.ui.cart.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.presentation.common.UiState
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel
import woowacourse.shopping.presentation.ui.cart.model.CartUiState

@BindingAdapter("cartImgUrl")
fun ImageView.setCartImgUrl(url: String?) {
    Glide.with(this.context).load(url).into(this)
}

@BindingAdapter("cartOrderCount")
fun TextView.setCartOrderCount(cartUiState: CartUiState?) {
    if (cartUiState == null) return
    this.text = cartUiState.cartProductUiModels.filter {
        it.isChecked
    }.sumOf { it.cartProduct.quantity }.toString()
}

@BindingAdapter("cartPrice")
fun TextView.setCartPrice(cartUiState: CartUiState?) {
    if(cartUiState == null) return
    this.text = this.context.getString(
        R.string.won,
        cartUiState.cartProductUiModels.sumOf {
            it.cartProduct.quantity * it.cartProduct.price
        },
    )
}
