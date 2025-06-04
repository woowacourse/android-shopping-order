package woowacourse.shopping.presentation.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendProductBinding
import woowacourse.shopping.presentation.CartItemUiModel
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.product.ItemClickListener

class RecommendViewHolder(
    private val binding: ItemRecommendProductBinding,
    itemClickListener: ItemClickListener,
    counterClickListener: CartCounterClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.itemClickListener = itemClickListener
        binding.counterClickListener = counterClickListener
    }

    fun bind(cartItemUiModel: CartItemUiModel) {
        binding.cartItemUiModel = cartItemUiModel
    }

    companion object {
        fun create(
            parent: ViewGroup,
            itemClickListener: ItemClickListener,
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
