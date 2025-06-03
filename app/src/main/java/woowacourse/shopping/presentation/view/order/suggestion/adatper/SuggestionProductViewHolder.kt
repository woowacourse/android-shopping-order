package woowacourse.shopping.presentation.view.order.suggestion.adatper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemSuggestionBinding
import woowacourse.shopping.presentation.model.SuggestionProductUiModel
import woowacourse.shopping.presentation.view.order.suggestion.event.SuggestionStateListener

class SuggestionProductViewHolder(
    private val binding: ItemSuggestionBinding,
    eventListener: SuggestionStateListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventListener = eventListener
    }

    fun bind(item: SuggestionProductUiModel) {
        binding.product = item
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: SuggestionStateListener,
        ): SuggestionProductViewHolder {
            val binding =
                ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SuggestionProductViewHolder(binding, eventListener)
        }
    }
}
