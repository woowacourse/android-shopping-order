package woowacourse.shopping.presentation.product

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.Product

class RecentProductAdapter(
    private val itemClickListener: ItemClickListener,
) : ListAdapter<Product, RecentProductViewHolder>(RecentProductDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder = RecentProductViewHolder.create(parent, itemClickListener)

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
