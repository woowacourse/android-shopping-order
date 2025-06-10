package woowacourse.shopping.presentation.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendProductBinding
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.model.CartItemUiModel

class RecommendViewHolder(
    private val binding: ItemRecommendProductBinding,
    itemClickListener: RecommendClickListener,
    counterClickListener: CartCounterClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.itemClickListener = itemClickListener
        binding.counterClickListener = counterClickListener
    }

    fun bind(cartItemUiModel: CartItemUiModel) {
        binding.cartItem = cartItemUiModel
    }

    companion object {
        fun create(
            parent: ViewGroup,
            itemClickListener: RecommendClickListener,
            counterClickListener: CartCounterClickListener,
        ): RecommendViewHolder =
            RecommendViewHolder(
                binding =
                    ItemRecommendProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                itemClickListener = itemClickListener,
                counterClickListener = counterClickListener,
            )
    }
}
