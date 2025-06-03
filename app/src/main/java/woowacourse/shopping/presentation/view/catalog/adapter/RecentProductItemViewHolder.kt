package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.catalog.CatalogEventHandler

class RecentProductItemViewHolder private constructor(
    private val binding: ItemRecentProductBinding,
    private val catalogEventHandler: CatalogEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: ProductUiModel) {
        binding.product = product
        binding.eventHandler = catalogEventHandler
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventHandler: CatalogEventHandler,
        ): RecentProductItemViewHolder {
            val binding =
                ItemRecentProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return RecentProductItemViewHolder(binding, eventHandler)
        }
    }
}
