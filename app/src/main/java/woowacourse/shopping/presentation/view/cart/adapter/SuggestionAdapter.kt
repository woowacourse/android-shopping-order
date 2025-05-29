package woowacourse.shopping.presentation.view.cart.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.SuggestionProductUiModel
import woowacourse.shopping.presentation.ui.layout.QuantityChangeListener

class SuggestionAdapter(
    private val eventListener: SuggestionEventListener,
) : ListAdapter<SuggestionProductUiModel, SuggestionProductViewHolder>(SuggestionProductDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SuggestionProductViewHolder = SuggestionProductViewHolder.from(parent, eventListener)

    override fun onBindViewHolder(
        holder: SuggestionProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    interface SuggestionEventListener : QuantityChangeListener {
        fun onQuantitySelectorOpenButtonClick(productId: Long)
    }
}
