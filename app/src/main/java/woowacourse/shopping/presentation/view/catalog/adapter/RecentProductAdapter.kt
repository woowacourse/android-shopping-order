package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.catalog.CatalogEventListener

class RecentProductAdapter(
    private val eventListener: CatalogEventListener,
) : ListAdapter<ProductUiModel, RecentProductItemViewHolder>(
        object : DiffUtil.ItemCallback<ProductUiModel>() {
            override fun areItemsTheSame(
                oldItem: ProductUiModel,
                newItem: ProductUiModel,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ProductUiModel,
                newItem: ProductUiModel,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductItemViewHolder = RecentProductItemViewHolder.from(parent, eventListener)

    override fun onBindViewHolder(
        holder: RecentProductItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
