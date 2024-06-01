package woowacourse.shopping.view.home.adapter.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.view.home.HomeClickListener

class RecentProductAdapter(private val clickListener: HomeClickListener) :
    ListAdapter<RecentProduct, RecentProductViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentProductViewHolder {
        return RecentProductViewHolder(
            ItemRecentProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecentProductViewHolder,
        position: Int,
    ) {
        val recentViewedProduct = currentList[position]
        return holder.bind(recentViewedProduct, clickListener)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RecentProduct>() {
            override fun areContentsTheSame(oldItem: RecentProduct, newItem: RecentProduct) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: RecentProduct, newItem: RecentProduct) =
                oldItem.productId == newItem.productId
        }
    }
}
