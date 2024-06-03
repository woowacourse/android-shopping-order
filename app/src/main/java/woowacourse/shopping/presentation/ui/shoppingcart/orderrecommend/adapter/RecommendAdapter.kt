package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.common.ProductCountHandler

class RecommendAdapter(
    private val productCountHandler: ProductCountHandler,
) : ListAdapter<Cart, RecommendAdapter.RecommendViewHolder>(CartDiffCallback) {
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
            cart: Cart,
            position: Int,
        ) {
            binding.product = cart.product
            binding.position = position
            binding.productCountHandler = productCountHandler
            binding.executePendingBindings()
        }
    }

    object CartDiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(
            oldItem: Cart,
            newItem: Cart,
        ): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(
            oldItem: Cart,
            newItem: Cart,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
