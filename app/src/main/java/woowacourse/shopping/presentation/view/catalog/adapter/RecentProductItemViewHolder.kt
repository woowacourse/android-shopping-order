package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.catalog.CatalogEventListener

class RecentProductItemViewHolder private constructor(
    private val binding: ItemRecentProductBinding,
    private val eventListener: CatalogEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: ProductUiModel) {
        binding.product = product
        binding.eventListener = eventListener
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: CatalogEventListener,
        ): RecentProductItemViewHolder {
            val binding =
                ItemRecentProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RecentProductItemViewHolder(binding, eventListener)
        }
    }
}
