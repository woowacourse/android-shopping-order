package woowacourse.shopping.product.catalog

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class RecentlyViewedProductAdapter(
    private val recentlyViewedProductClickListener: RecentlyViewedProductClickListener,
) : ListAdapter<ProductUiModel, RecentlyViewedProductViewHolder>(
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
    ): RecentlyViewedProductViewHolder = RecentlyViewedProductViewHolder.from(parent, recentlyViewedProductClickListener)

    override fun onBindViewHolder(
        holder: RecentlyViewedProductViewHolder,
        position: Int,
    ) = holder.bind(currentList[position])
}
