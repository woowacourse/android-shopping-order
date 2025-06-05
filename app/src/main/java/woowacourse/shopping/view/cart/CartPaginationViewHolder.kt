package woowacourse.shopping.view.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartPaginationBinding

class CartPaginationViewHolder(
    private val binding: ItemShoppingCartPaginationBinding,
    onCartPaginationListener: OnCartPaginationListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onShoppingCartPaginationListener = onCartPaginationListener
    }

    fun bind(item: CartItemType.PaginationItem) {
        binding.paginationItem = item
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onCartPaginationListener: OnCartPaginationListener,
        ): CartPaginationViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemShoppingCartPaginationBinding.inflate(layoutInflater, parent, false)
            return CartPaginationViewHolder(
                binding,
                onCartPaginationListener,
            )
        }
    }
}
