package woowacourse.shopping.presentation.view.cart.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.SuggestionProductUiModel

object SuggestionProductDiffUtil : DiffUtil.ItemCallback<SuggestionProductUiModel>() {
    override fun areItemsTheSame(
        oldItem: SuggestionProductUiModel,
        newItem: SuggestionProductUiModel,
    ): Boolean = oldItem.productId == newItem.productId

    override fun areContentsTheSame(
        oldItem: SuggestionProductUiModel,
        newItem: SuggestionProductUiModel,
    ): Boolean = oldItem == newItem
}
