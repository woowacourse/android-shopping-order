package woowacourse.shopping.view.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartItemSkeletonBinding
import woowacourse.shopping.databinding.ItemShoppingCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.view.cart.OnClickNavigateShoppingCart
import woowacourse.shopping.view.cart.OnClickShoppingCart
import woowacourse.shopping.view.cart.adapter.viewholder.ShoppingCartSkeletonViewHolder
import woowacourse.shopping.view.cart.adapter.viewholder.ShoppingCartViewHolder
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter

class ShoppingCartAdapter(
    private val onClickShoppingCart: OnClickShoppingCart,
    private val onClickCartItemCounter: OnClickCartItemCounter,
    private val onClickNavigateShoppingCart: OnClickNavigateShoppingCart,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cartItems: MutableList<ShoppingCartItem> = mutableListOf()
    private var showSkeleton: Boolean = true
    private val productPosition: HashMap<Long, Int> = hashMapOf()

    override fun getItemViewType(position: Int): Int {
        return when (cartItems[position]) {
            is ShoppingCartItem.CartProductItem -> VIEW_TYPE_CART_PRODUCT
            else -> VIEW_TYPE_SKELETON
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CART_PRODUCT -> {
                val view =
                    ItemShoppingCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ShoppingCartViewHolder(view, onClickCartItemCounter, onClickShoppingCart, onClickNavigateShoppingCart)
            }
            else -> {
                val view =
                    ItemCartItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ShoppingCartSkeletonViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ShoppingCartViewHolder && position < cartItems.size) {
            val item = (cartItems[position] as ShoppingCartItem.CartProductItem).cartItem
            productPosition[item.product.id] = position
            item.product.updateItemSelector(true)
            holder.bind(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCartItems(addedCartItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(addedCartItems.map { ShoppingCartItem.CartProductItem(it) })
        if (showSkeleton) {
            cartItems.addAll(List(SKELETON_COUNT) { ShoppingCartItem.SkeletonItem })
        }
        notifyDataSetChanged()
    }

    fun updateCartItem(productId: Long) {
        val position = productPosition[productId]
        if (position != null) {
            notifyItemChanged(position)
        }
    }

    fun deleteCartItem(productId: Long) {
        val position = productPosition[productId]
        if (position != null) {
            notifyItemChanged(position)
            productPosition.remove(productId)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setShowSkeleton(show: Boolean) {
        showSkeleton = show
        if (showSkeleton) {
            cartItems.addAll(List(SKELETON_COUNT) { ShoppingCartItem.SkeletonItem })
        } else {
            cartItems = cartItems.filter { it !is ShoppingCartItem.SkeletonItem }.toMutableList()
        }
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_CART_PRODUCT = 0
        private const val VIEW_TYPE_SKELETON = 1
        private const val SKELETON_COUNT = 10
    }
}
