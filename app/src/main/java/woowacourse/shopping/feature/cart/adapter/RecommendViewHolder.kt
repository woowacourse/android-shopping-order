package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendGoodsBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener

class RecommendViewHolder(
    private val binding: ItemRecommendGoodsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cartItem: CartItem) {
        binding.cartItem = cartItem
        binding.executePendingBindings()
    }

    companion object {
        fun from(
            parent: ViewGroup,
            quantityChangeListener: QuantityChangeListener,
            lifecycleOwner: LifecycleOwner
        ): RecommendViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemRecommendGoodsBinding.inflate(inflater, parent, false)
            binding.quantityChangeListener = quantityChangeListener
            binding.lifecycleOwner = lifecycleOwner
            return RecommendViewHolder(binding)
        }
    }
}