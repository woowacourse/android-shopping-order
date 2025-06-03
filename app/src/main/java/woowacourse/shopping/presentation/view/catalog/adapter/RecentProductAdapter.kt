package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.ProductUiModel

class RecentProductAdapter(
    private val eventListener: CatalogAdapter.CatalogEventListener,
) : ListAdapter<ProductUiModel, RecentProductViewHolder>(RecentProductDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder = RecentProductViewHolder.from(parent, eventListener)

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }
}
