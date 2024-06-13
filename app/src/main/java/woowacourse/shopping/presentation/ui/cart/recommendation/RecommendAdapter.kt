package woowacourse.shopping.presentation.ui.cart.recommendation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class RecommendAdapter(
    private val recommendItemCountHandler: CounterHandler,
) : ListAdapter<ShoppingProduct, RecommendViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), recommendItemCountHandler)
    }

    companion object {
        val diffCallback =
            object : DiffUtil.ItemCallback<ShoppingProduct>() {
                override fun areItemsTheSame(
                    oldItem: ShoppingProduct,
                    newItem: ShoppingProduct,
                ): Boolean {
                    return oldItem.product.id == newItem.product.id
                }

                override fun areContentsTheSame(
                    oldItem: ShoppingProduct,
                    newItem: ShoppingProduct,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
