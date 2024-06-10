package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.ui.cart.CartItemClickListener
import woowacourse.shopping.ui.cart.CartUiModel
import woowacourse.shopping.ui.listener.CountButtonClickListener

class CartAdapter(
    private val cartItemClickListener: CartItemClickListener,
    private val countButtonClickListener: CountButtonClickListener,
) : ListAdapter<CartUiModel, CartViewHolder>(CartDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(
            binding,
            cartItemClickListener,
            countButtonClickListener,
        )
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
