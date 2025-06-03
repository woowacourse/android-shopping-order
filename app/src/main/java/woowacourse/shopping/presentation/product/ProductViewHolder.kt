package woowacourse.shopping.presentation.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.presentation.cart.CartCounterClickListener

class ProductViewHolder private constructor(
    private val binding: ItemProductBinding,
    cartCounterClickListener: CartCounterClickListener,
    itemClickListener: ItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.itemClickListener = itemClickListener
        binding.counterClickListener = cartCounterClickListener
    }

    fun bind(item: ProductItemType.Product) {
        binding.cartItemUiModel = item.cartItemUiModel
        binding.executePendingBindings()
    }

    companion object {
        fun create(
            parent: ViewGroup,
            cartCounterClickListener: CartCounterClickListener,
            itemClickListener: ItemClickListener,
        ): ProductViewHolder =
            ProductViewHolder(
                binding =
                    ItemProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                cartCounterClickListener = cartCounterClickListener,
                itemClickListener = itemClickListener,
            )
    }
}
