package woowacourse.shopping.presentation.view.catalog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.ProductUiModel

class RecentProductAdapter(
    private val eventListener: CatalogAdapter.CatalogEventListener,
) : ListAdapter<ProductUiModel, RecentProductViewHolder>(DiffCallBack) {
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

    object DiffCallBack : DiffUtil.ItemCallback<ProductUiModel>() {
        override fun areItemsTheSame(
            oldItem: ProductUiModel,
            newItem: ProductUiModel,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ProductUiModel,
            newItem: ProductUiModel,
        ): Boolean = oldItem == newItem
    }
}
