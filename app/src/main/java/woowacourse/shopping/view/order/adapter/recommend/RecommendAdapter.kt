package woowacourse.shopping.view.order.adapter.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.view.order.viewmodel.OrderViewModel

class RecommendAdapter(
    private val viewModel: OrderViewModel,
) : ListAdapter<ProductViewItem, RecommendViewHolder>(diffUtil) {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        return RecommendViewHolder(
            ItemRecommendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position], viewModel)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<ProductViewItem>() {
                override fun areContentsTheSame(
                    oldItem: ProductViewItem,
                    newItem: ProductViewItem,
                ) = oldItem == newItem

                override fun areItemsTheSame(
                    oldItem: ProductViewItem,
                    newItem: ProductViewItem,
                ) = oldItem.product.productId == newItem.product.productId
            }
    }
}
