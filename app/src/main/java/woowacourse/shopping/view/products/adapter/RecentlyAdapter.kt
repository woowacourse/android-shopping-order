package woowacourse.shopping.view.products.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecentlyProductBinding
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.view.products.OnClickProducts
import woowacourse.shopping.view.products.adapter.viewholder.RecentlyViewHolder


class RecentlyAdapter(
    private val onClickProducts: OnClickProducts,
) : ListAdapter<RecentlyProduct, RecentlyViewHolder>(RecentlyProductDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentlyViewHolder {
        val view = ItemRecentlyProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentlyViewHolder(view, onClickProducts)
    }

    override fun onBindViewHolder(
        holder: RecentlyViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    class RecentlyProductDiffCallback : DiffUtil.ItemCallback<RecentlyProduct>() {
        override fun areItemsTheSame(oldItem: RecentlyProduct, newItem: RecentlyProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecentlyProduct, newItem: RecentlyProduct): Boolean {
            return oldItem == newItem
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(recentlyProducts: List<RecentlyProduct>) {
        submitList(recentlyProducts)
    }
}
