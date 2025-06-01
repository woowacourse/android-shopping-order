package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentProductContainerBinding
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.catalog.CatalogEventListener

class RecentProductViewHolder(
    binding: ItemRecentProductContainerBinding,
    eventListener: CatalogEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    private val adapter = RecentProductAdapter(eventListener)

    init {
        binding.recyclerViewRecentProduct.adapter = adapter
    }

    fun bind(products: List<ProductUiModel>) {
        adapter.submitList(products)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: CatalogEventListener,
        ): RecentProductViewHolder {
            val binding =
                ItemRecentProductContainerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return RecentProductViewHolder(binding, eventListener)
        }
    }
}
