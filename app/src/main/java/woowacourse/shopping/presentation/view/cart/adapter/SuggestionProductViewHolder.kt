package woowacourse.shopping.presentation.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemSuggestionBinding
import woowacourse.shopping.presentation.model.SuggestionProductUiModel

class SuggestionProductViewHolder(
    private val binding: ItemSuggestionBinding,
    eventListener: SuggestionAdapter.SuggestionEventListener,
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
            eventListener: SuggestionAdapter.SuggestionEventListener,
        ): SuggestionProductViewHolder {
            val binding =
                ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SuggestionProductViewHolder(binding, eventListener)
        }
    }
}
