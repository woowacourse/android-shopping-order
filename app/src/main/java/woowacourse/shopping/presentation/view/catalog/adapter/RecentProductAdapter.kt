package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.catalog.CatalogEventHandler

class RecentProductAdapter(
    private val catalogEventHandler: CatalogEventHandler,
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
    ): RecentProductItemViewHolder = RecentProductItemViewHolder.from(parent, catalogEventHandler)

    override fun onBindViewHolder(
        holder: RecentProductItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
