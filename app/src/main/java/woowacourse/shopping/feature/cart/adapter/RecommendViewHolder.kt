package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.domain.model.CartProduct

class RecommendViewHolder(
    private val binding: ItemRecommendBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cart: CartProduct) {
        binding.cart = cart
    }

    companion object {
        fun from(parent: ViewGroup): RecommendViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecommendBinding.inflate(layoutInflater, parent, false)
            return RecommendViewHolder(binding)
        }
    }
}
