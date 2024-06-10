package woowacourse.shopping.presentation.ui.detail.adapter

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.ui.detail.model.DetailUiState

@BindingAdapter("productPrice")
fun TextView.setProductPrice(cartProduct: CartProduct?) {
    cartProduct?.let {
        this.text = this.context.getString(R.string.won, it.price * it.quantity)
    }
}

@BindingAdapter("detailRecentVisibility")
fun View.setDetailRecentVisibility(detailUiState: DetailUiState?) {
    this.isVisible = detailUiState?.run {
        !isLast && recentProduct != null
    } ?: true
}
