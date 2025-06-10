package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentProductContainerBinding
import woowacourse.shopping.presentation.common.model.CatalogItem
import woowacourse.shopping.presentation.common.ui.decorations.HorizontalEdgeSpacingDecoration

class RecentProductContainerViewHolder private constructor(
    private val binding: ItemRecentProductContainerBinding,
    adapter: RecentProductAdapter,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.recyclerViewRecentProduct.adapter = adapter
        binding.recyclerViewRecentProduct.addItemDecoration(HorizontalEdgeSpacingDecoration())
    }

    fun bind(recentProducts: CatalogItem.RecentProducts) {
        binding.recentProducts = recentProducts.products
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
