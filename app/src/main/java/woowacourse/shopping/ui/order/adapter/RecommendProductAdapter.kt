package woowacourse.shopping.ui.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product

class RecommendProductAdapter(
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
) : ListAdapter<Product, RecommendProductViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HolderProductBinding.inflate(inflater, parent, false)
        return RecommendProductViewHolder(binding, onItemQuantityChangeListener)
    }

    override fun onBindViewHolder(
        holder: RecommendProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    fun updateRecommendProducts(newRecommendProducts: List<Product>) {
        submitList(newRecommendProducts)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<Product>() {
                override fun areItemsTheSame(
                    oldItem: Product,
                    newItem: Product,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Product,
                    newItem: Product,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
