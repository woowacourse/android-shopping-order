package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentProductContainerBinding
import woowacourse.shopping.presentation.model.CatalogItem
import woowacourse.shopping.presentation.ui.decorations.HorizontalEdgeSpacingDecoration

class RecentProductContainerViewHolder private constructor(
    binding: ItemRecentProductContainerBinding,
    private val adapter: RecentProductAdapter,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.recyclerViewRecentProduct.adapter = adapter
        binding.recyclerViewRecentProduct.addItemDecoration(HorizontalEdgeSpacingDecoration())
    }

    fun bind(recentProducts: CatalogItem.RecentProducts) {
        adapter.submitList(recentProducts.products)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            recentProductAdapter: RecentProductAdapter,
        ): RecentProductContainerViewHolder {
            val binding =
                ItemRecentProductContainerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return RecentProductContainerViewHolder(binding, recentProductAdapter)
        }
    }
}
