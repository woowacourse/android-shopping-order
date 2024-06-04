package woowacourse.shopping.presentation.ui.curation.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.ui.UiState

@BindingAdapter("curationPrice")
fun TextView.setPrice(uiState: UiState<List<CartProduct>>) {
    if (uiState is UiState.Success) {
        this.text =
            this.context.getString(
                R.string.won,
                uiState.data.sumOf {
                    it.quantity * it.price
                },
            )
    }
}

@BindingAdapter("curationOrderCount")
fun TextView.setOrderCount(uiState: UiState<List<CartProduct>>) {
    if (uiState is UiState.Success) {
        this.text =
            uiState.data.sumOf {
                it.quantity
            }.toString()
    }
}
