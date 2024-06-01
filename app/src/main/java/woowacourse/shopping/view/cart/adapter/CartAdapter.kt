package woowacourse.shopping.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.databinding.ItemCartPlaceholderBinding
import woowacourse.shopping.view.cart.CartItemClickListener
import woowacourse.shopping.view.cart.QuantityClickListener
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartPlaceHolderViewItem
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem

class CartAdapter(
    private val cartItemClickListener: CartItemClickListener,
    private val quantityClickListener: QuantityClickListener,
) : ListAdapter<ShoppingCartViewItem, RecyclerView.ViewHolder>(diffUtil) {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
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
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val cartViewItem = currentList[position]
        if (holder is CartViewHolder) {
            holder.bind(
                cartViewItem as CartViewItem,
                cartItemClickListener,
                quantityClickListener,
            )
        }
    }

    fun submitCartViewItems(cartItems: List<CartViewItem>) {
        val list = if (currentList.isEmpty()) {
            List(5) { CartPlaceHolderViewItem() }
        } else {
            cartItems
        }
        super.submitList(list)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ShoppingCartViewItem>() {
            override fun areContentsTheSame(
                oldItem: ShoppingCartViewItem,
                newItem: ShoppingCartViewItem
            ) = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: ShoppingCartViewItem,
                newItem: ShoppingCartViewItem
            ): Boolean {
                return if (oldItem is CartViewItem && newItem is CartViewItem) {
                    oldItem.cartItem.cartItemId == newItem.cartItem.cartItemId
                } else {
                    oldItem.viewType == newItem.viewType
                }
            }
        }
    }
}
