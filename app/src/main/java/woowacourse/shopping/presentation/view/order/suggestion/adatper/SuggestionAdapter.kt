package woowacourse.shopping.presentation.view.order.suggestion.adatper

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.SuggestionProductUiModel
import woowacourse.shopping.presentation.view.order.suggestion.event.SuggestionStateListener

class SuggestionAdapter(
    private val eventListener: SuggestionStateListener,
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
}
