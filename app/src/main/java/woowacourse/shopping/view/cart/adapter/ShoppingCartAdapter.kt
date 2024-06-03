package woowacourse.shopping.view.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartItemSkeletonBinding
import woowacourse.shopping.databinding.ItemShoppingCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.view.cart.OnClickNavigateShoppingCart
import woowacourse.shopping.view.cart.OnClickShoppingCart
import woowacourse.shopping.view.cart.adapter.viewholder.ShoppingCartSkeletonViewHolder
import woowacourse.shopping.view.cart.adapter.viewholder.ShoppingCartViewHolder
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter
import woowacourse.shopping.view.products.model.ShoppingItem

class ShoppingCartAdapter(
    private val onClickShoppingCart: OnClickShoppingCart,
    private val onClickCartItemCounter: OnClickCartItemCounter,
    private val onClickNavigateShoppingCart: OnClickNavigateShoppingCart,
) : ListAdapter<ShoppingCartItem, RecyclerView.ViewHolder>(ShoppingCartDiffCallback()) {

    private var showSkeleton: Boolean = true

    class ShoppingCartDiffCallback : DiffUtil.ItemCallback<ShoppingCartItem>() {
        override fun areItemsTheSame(oldItem: ShoppingCartItem, newItem: ShoppingCartItem): Boolean {
            return if (oldItem is ShoppingCartItem.CartProductItem && newItem is ShoppingCartItem.CartProductItem) {
                oldItem.cartItem.product.id == newItem.cartItem.product.id
            } else {
                oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItem: ShoppingCartItem, newItem: ShoppingCartItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
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
                val view = ItemShoppingCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ShoppingCartViewHolder(view, onClickCartItemCounter, onClickShoppingCart, onClickNavigateShoppingCart)
            }
            else -> {
                val view = ItemCartItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ShoppingCartSkeletonViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ShoppingCartViewHolder && position < currentList.size) {
            val item = (getItem(position) as ShoppingCartItem.CartProductItem).cartItem
            holder.bind(item)
        }
    }

    fun updateCartItems(addedCartItems: List<CartItem>) {
        val updatedItems = mutableListOf<ShoppingCartItem>()
        updatedItems.addAll(addedCartItems.map { ShoppingCartItem.CartProductItem(it) })
        if (showSkeleton) {
            updatedItems.addAll(List(SKELETON_COUNT) { ShoppingCartItem.SkeletonItem })
        }
        submitList(updatedItems)
    }

    fun updateCartItem(productId: Long) {
        val position = currentList.indexOfFirst {
            it is ShoppingCartItem.CartProductItem && it.cartItem.product.id == productId
        }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    fun deleteCartItem(productId: Long) {
        val position = currentList.indexOfFirst {
            it is ShoppingCartItem.CartProductItem && it.cartItem.product.id == productId
        }
        if (position != -1) {
            notifyItemRemoved(position)
        }
    }

    fun setShowSkeleton(show: Boolean) {
        showSkeleton = show
        val currentListWithoutSkeletons = currentList.filter { it !is ShoppingCartItem.SkeletonItem }
        if (showSkeleton) {
            val newList = currentListWithoutSkeletons.toMutableList()
            newList.addAll(List(SKELETON_COUNT) { ShoppingCartItem.SkeletonItem })
            submitList(newList)
        } else {
            submitList(currentListWithoutSkeletons)
        }
    }

    companion object {
        private const val VIEW_TYPE_CART_PRODUCT = 0
        private const val VIEW_TYPE_SKELETON = 1
        private const val SKELETON_COUNT = 10
    }
}
