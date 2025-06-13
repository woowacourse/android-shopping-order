package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.feature.CustomCartQuantity

class RecommendViewHolder(
    private val binding: ItemRecommendBinding,
    private val recommendClickListener: RecommendClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var currentCart: CartProduct

    init {
        binding.recommendClickListener = recommendClickListener
        binding.customCartQuantity.setClickListener(
            object : CustomCartQuantity.CartQuantityClickListener {
                override fun onAddClick() {
                    recommendClickListener.insertToCart(currentCart)
                }

                override fun onRemoveClick() {
                    recommendClickListener.removeFromCart(currentCart)
                }
            },
        )
    }

    fun bind(cart: CartProduct) {
        currentCart = cart
        binding.cart = cart
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: RecommendClickListener,
        ): RecommendViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecommendBinding.inflate(layoutInflater, parent, false)
            return RecommendViewHolder(binding, listener)
        }
    }
}
