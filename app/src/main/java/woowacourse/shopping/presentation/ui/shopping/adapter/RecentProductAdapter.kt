package woowacourse.shopping.presentation.ui.shopping.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemRecentProductBinding
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.presentation.ui.shopping.ShoppingEventHandler
import woowacourse.shopping.presentation.ui.shopping.adapter.viewholder.RecentViewHolder

class RecentProductAdapter(
    private val onClickProducts: ShoppingEventHandler,
) : ListAdapter<RecentProduct, RecentViewHolder>(diffCallback) {
    private var recentProducts: List<RecentProduct> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentViewHolder {
        val view = ItemRecentProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(view, onClickProducts)
    }

    override fun onBindViewHolder(
        holder: RecentViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun loadRecentProductData(recentProducts: List<RecentProduct>) {
        this.recentProducts = recentProducts
    }

    companion object {
        val diffCallback =
            object : DiffUtil.ItemCallback<RecentProduct>() {
                override fun areItemsTheSame(
                    oldItem: RecentProduct,
                    newItem: RecentProduct,
                ): Boolean {
                    return oldItem.productId == newItem.productId
                }

                override fun areContentsTheSame(
                    oldItem: RecentProduct,
                    newItem: RecentProduct,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
