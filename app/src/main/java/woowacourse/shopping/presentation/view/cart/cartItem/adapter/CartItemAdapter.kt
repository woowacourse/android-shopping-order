package woowacourse.shopping.presentation.view.cart.cartItem.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.view.cart.cartItem.CartItemEventHandler
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler

class CartItemAdapter(
    private val cartItemEventHandler: CartItemEventHandler,
    private val itemCounterEventHandler: ItemCounterEventHandler,
) : ListAdapter<CartItemUiModel, CartItemViewHolder>(
        object : DiffUtil.ItemCallback<CartItemUiModel>() {
            override fun areItemsTheSame(
                oldItem: CartItemUiModel,
                newItem: CartItemUiModel,
            ): Boolean = oldItem.cartItem.cartId == newItem.cartItem.cartId

            override fun areContentsTheSame(
                oldItem: CartItemUiModel,
                newItem: CartItemUiModel,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartItemViewHolder = CartItemViewHolder.from(parent, cartItemEventHandler, itemCounterEventHandler)

    override fun onBindViewHolder(
        holder: CartItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateItem(updatedItem: CartItemUiModel) {
        val newList =
            currentList.map {
                if (it.cartItem.cartId == updatedItem.cartItem.cartId) updatedItem else it
            }
        submitList(newList)
    }

    fun removeProduct(cartId: Long) {
        val newList =
            currentList.filterNot {
                it.cartItem.cartId == cartId
            }
        submitList(newList)
    }
}
