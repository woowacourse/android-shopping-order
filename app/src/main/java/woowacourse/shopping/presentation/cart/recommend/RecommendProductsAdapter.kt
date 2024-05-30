package woowacourse.shopping.presentation.cart.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendProductBinding
import woowacourse.shopping.presentation.cart.CartItemListener
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.util.ItemDiffCallback

class RecommendProductsAdapter(
    private val cartItemListener: CartItemListener,
) :
    ListAdapter<CartProductUi, RecommendProductsAdapter.RecommendProductsViewHolder>(
            cartProductComparator,
        ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductsViewHolder {
        val binding =
            ItemRecommendProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return RecommendProductsViewHolder(binding, cartItemListener)
    }

    override fun onBindViewHolder(
        holder: RecommendProductsViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    class RecommendProductsViewHolder(
        private val binding: ItemRecommendProductBinding,
        private val cartItemListener: CartItemListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: CartProductUi) {
            binding.cartProduct = product
            binding.listener = cartItemListener
        }
    }

    companion object {
        private val cartProductComparator =
            ItemDiffCallback<CartProductUi>(
                onItemsTheSame = { old, new -> old.product.id == new.product.id },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}
