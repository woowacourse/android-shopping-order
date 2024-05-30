package woowacourse.shopping.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.model.CartItem2
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.databinding.ItemCartPlaceholderBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.view.cart.CartItemClickListener
import woowacourse.shopping.view.cart.QuantityClickListener
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem

class CartAdapter(
    private val cartItemClickListener: CartItemClickListener,
    private val quantityClickListener: QuantityClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cartItems: MutableList<ShoppingCartViewItem> =
        MutableList(5) { ShoppingCartViewItem.CartPlaceHolderViewItem() }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
    }

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
                cartViewItem as CartViewItem,
                cartItemClickListener,
                quantityClickListener,
            )
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun loadData(cartItems: List<CartViewItem>) {
        this.cartItems.clear()
        this.cartItems.addAll(cartItems)

        notifyDataSetChanged()
    }

    fun updateCartItemQuantity(cartItem: CartItem2) {
        if (!isFirstLoad()) {
            println("input : $cartItem")
            val position =
                cartItems.indexOfFirst { (it as CartViewItem).cartItem.cartItemId == cartItem.cartItemId }
            if (position != -1) {
                println("unchanged : ${cartItems[position]}")
                cartItems[position] = CartViewItem(cartItem)

                notifyItemChanged(position)
                println("changed : ${cartItems[position]}")
            }
        }
    }

    fun updateSelection(changedItemId: Int) {
        val position = cartItems.indexOfFirst {
            if (it is CartViewItem) {
                it.cartItem.cartItemId == changedItemId
            } else {
                false
            }
        }
        (cartItems[position] as CartViewItem).select()
        notifyDataSetChanged()
    }

    private fun isFirstLoad() =
        cartItems.all { it.viewType == ShoppingCartViewItem.CART_PLACEHOLDER_VIEW_TYPE }
}
