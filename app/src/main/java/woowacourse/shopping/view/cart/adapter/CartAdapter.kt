package woowacourse.shopping.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.databinding.ItemCartPlaceholderBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.view.cart.CartItemClickListener
import woowacourse.shopping.view.cart.CartViewModel.Companion.PAGE_SIZE
import woowacourse.shopping.view.cart.QuantityClickListener
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem

class CartAdapter(
    private val cartItemClickListener: CartItemClickListener,
    private val quantityClickListener: QuantityClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cartItems: List<ShoppingCartViewItem> =
        List(5) { ShoppingCartViewItem.CartPlaceHolderViewItem() }

    override fun getItemViewType(position: Int): Int {
        return cartItems[position].viewType
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        if (viewType == ShoppingCartViewItem.CART_VIEW_TYPE) {
            return CartViewHolder(
                ItemCartBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
            )
        }
        return CartPlaceholderViewHolder(
            ItemCartPlaceholderBinding.inflate(
                LayoutInflater.from(
                    parent.context,
                ),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val cartViewItem = cartItems[position]
        if (holder is CartViewHolder) {
            holder.bind(
                (cartViewItem as CartViewItem).cartItem,
                cartItemClickListener,
                quantityClickListener,
            )
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun loadData(cartItems: List<CartItem>) {
        val newCartItems = cartItems.map { CartViewItem(it) }
        val oldCartItems = this.cartItems
        this.cartItems = newCartItems

        val oldSize = oldCartItems.size
        val newSize = newCartItems.size

        if ((oldSize == 1 && newSize == PAGE_SIZE) || (oldSize == PAGE_SIZE && newSize >= 1)) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeChanged(0, newSize)
        } else {
            if (oldSize < newSize) {
                notifyItemInserted(oldSize + 1)
            } else {
                val removedItem = oldCartItems.find { !newCartItems.contains(it) }
                val removedPosition = oldCartItems.indexOf(removedItem)
                notifyItemRangeRemoved(removedPosition, oldSize - removedPosition)
            }
        }
    }

    fun updateCartItemQuantity(cartItem: CartItem) {
        if (!isFirstLoad()) {
            val position = cartItems.indexOfFirst { (it as CartViewItem).cartItem.id == cartItem.id }
            if (position != -1) {
                cartItems.toMutableList()[position] = CartViewItem(cartItem)
                notifyItemChanged(position)
            }
        }
    }

    private fun isFirstLoad() = cartItems.all { it.viewType == ShoppingCartViewItem.CART_PLACEHOLDER_VIEW_TYPE }
}
