package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.common.ProductCountHandler

class RecommendAdapter(
    private val productCountHandler: ProductCountHandler,
) : ListAdapter<Product, RecommendAdapter.RecommendViewHolder>(ProductDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return RecommendViewHolder(
            HolderProductBinding.inflate(inflater, parent, false),
            productCountHandler,
        )
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), position)
    }

    class RecommendViewHolder(
        private val binding: HolderProductBinding,
        private val productCountHandler: ProductCountHandler,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            product: Product,
            position: Int,
        ) {
            binding.product = product
            binding.position = position
            binding.productCountHandler = productCountHandler
            binding.executePendingBindings()
        }
    }

    object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
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
