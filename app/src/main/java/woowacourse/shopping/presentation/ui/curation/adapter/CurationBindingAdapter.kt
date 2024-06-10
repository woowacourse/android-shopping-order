package woowacourse.shopping.presentation.ui.curation.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.common.UiState
import woowacourse.shopping.presentation.ui.curation.model.CurationUiState

@BindingAdapter("curationPrice")
fun TextView.setPrice(curationUiState: CurationUiState?) {
    if(curationUiState == null) return
    this.text =
        this.context.getString(
            R.string.won,
            curationUiState.cartProducts.sumOf {
                it.quantity * it.price
            },
        )
}

@BindingAdapter("curationOrderCount")
fun TextView.setOrderCount(curationUiState: CurationUiState?) {
    if(curationUiState == null) return
    this.text =
        curationUiState.cartProducts.sumOf {
            it.quantity
        }.toString()
}
