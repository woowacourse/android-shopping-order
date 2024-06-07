package woowacourse.shopping.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.HolderCartBinding
import woowacourse.shopping.common.OnItemQuantityChangeListener
import woowacourse.shopping.ui.cart.listener.OnCartItemDeleteListener
import woowacourse.shopping.ui.cart.listener.OnCartItemSelectedListener
import woowacourse.shopping.ui.model.CartItem

class CartItemAdapter(
    private val onCartItemDeleteListener: OnCartItemDeleteListener,
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
    private val onCartItemSelectedListener: OnCartItemSelectedListener,
) : ListAdapter<CartItem, CartItemViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartItemViewHolder =
        CartItemViewHolder(
            HolderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onCartItemDeleteListener,
            onItemQuantityChangeListener,
            onCartItemSelectedListener,
        )

    override fun onBindViewHolder(
        holder: CartItemViewHolder,
        position: Int,
    ) = holder.bind(getItem(position))

    fun updateCartItems(newData: List<CartItem>) {
        submitList(newData)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(
                oldItem: CartItem,
                newItem: CartItem,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CartItem,
                newItem: CartItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
