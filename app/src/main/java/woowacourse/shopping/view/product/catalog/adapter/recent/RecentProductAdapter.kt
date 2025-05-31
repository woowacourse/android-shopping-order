package woowacourse.shopping.view.product.catalog.adapter.recent

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.RecentProduct

class RecentProductAdapter(
    private val eventHandler: RecentProductViewHolder.EventHandler,
) : ListAdapter<RecentProduct, RecentProductViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder = RecentProductViewHolder.from(parent, eventHandler)

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<RecentProduct>() {
                override fun areItemsTheSame(
                    oldItem: RecentProduct,
                    newItem: RecentProduct,
                ): Boolean = oldItem.product.id == newItem.product.id

                override fun areContentsTheSame(
                    oldItem: RecentProduct,
                    newItem: RecentProduct,
                ): Boolean = oldItem == newItem
            }
    }
}
