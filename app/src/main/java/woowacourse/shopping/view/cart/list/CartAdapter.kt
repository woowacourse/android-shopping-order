package woowacourse.shopping.view.cart.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.databinding.ItemCartPlaceholderBinding
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.cart.list.ShoppingCartViewItem.CartViewItem

class CartAdapter(
    private val cartItemClickListener: CartItemClickListener,
    private val quantityClickListener: QuantityEventListener,
) : ListAdapter<ShoppingCartViewItem, RecyclerView.ViewHolder>(CartDiffUtil) {
    init {
        submitList(List(6) { ShoppingCartViewItem.CartPlaceHolderViewItem() })
    }

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
        val cartViewItem = currentList[position]
        if (holder is CartViewHolder) {
            holder.bind(
                cartViewItem as CartViewItem,
                cartItemClickListener,
                quantityClickListener,
            )
        }
    }

    fun loadData(cartItems: List<CartViewItem>) {
        if (isFirstLoad()) submitList(null)
        submitList(cartItems)
    }

    private fun isFirstLoad() = currentList.all { it.viewType == ShoppingCartViewItem.CART_PLACEHOLDER_VIEW_TYPE }
}
