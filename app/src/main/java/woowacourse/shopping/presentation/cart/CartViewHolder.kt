package woowacourse.shopping.presentation.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartProductBinding
import woowacourse.shopping.presentation.CartItemUiModel

class CartViewHolder private constructor(
    val binding: ItemCartProductBinding,
    cartCounterClickListener: CartCounterClickListener,
    cartPageClickListener: CartPageClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.cartPageClickListener = cartPageClickListener
        binding.counterClickListener = cartCounterClickListener
    }

    fun bind(cartItem: CartItemUiModel) {
        binding.cartItem = cartItem
        binding.executePendingBindings()
    }

    companion object {
        fun create(
            parent: ViewGroup,
            cartCounterClickListener: CartCounterClickListener,
            cartPageClickListener: CartPageClickListener,
        ): CartViewHolder =
            CartViewHolder(
                binding =
                    ItemCartProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                cartCounterClickListener = cartCounterClickListener,
                cartPageClickListener = cartPageClickListener,
            )
    }
}
