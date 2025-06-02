package woowacourse.shopping.feature.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.databinding.ItemCartSkeletonBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener

class CartAdapter(
    private val cartClickListener: CartViewHolder.CartClickListener,
    private val quantityChangeListener: QuantityChangeListener,
    private val onItemCheckedChange: (CartItem, Boolean) -> Unit,
    private val isItemChecked: (CartItem) -> Boolean,
) : ListAdapter<CartListItem, RecyclerView.ViewHolder>(CartDiffCallback()) {
    fun showSkeleton(count: Int = 5) {
        val skeletonItems = List(count) { CartListItem.Skeleton }
        submitList(skeletonItems)
    }

    fun setCartItems(cartItems: List<CartItem>) {
        val newItems = cartItems.map { CartListItem.CartData(it) }
        submitList(newItems)
    }

    fun removeItem(removeCartItem: CartItem) {
        val currentList = currentList.toMutableList()
        currentList.removeIf {
            it is CartListItem.CartData && it.cartItem.goods.id == removeCartItem.goods.id
        }
        submitList(currentList)
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is CartListItem.Skeleton -> TYPE_SKELETON
            is CartListItem.CartData -> TYPE_CART_ITEM
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_SKELETON -> {
                val binding = ItemCartSkeletonBinding.inflate(inflater, parent, false)
                SkeletonViewHolder(binding)
            }
            TYPE_CART_ITEM -> {
                val binding = ItemCartBinding.inflate(inflater, parent, false)
                binding.cartClickListener = cartClickListener
                binding.quantityChangeListener = quantityChangeListener
                CartViewHolder(binding)
            }
            else -> throw IllegalArgumentException("알 수 없는 뷰 타입: $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is CartListItem.Skeleton -> {}
            is CartListItem.CartData -> {
                val cartHolder = holder as CartViewHolder
                cartHolder.bind(item.cartItem)
                cartHolder.binding.checkBoxItem.setOnCheckedChangeListener(null)
                cartHolder.binding.checkBoxItem.isChecked = isItemChecked(item.cartItem)
                cartHolder.binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
                    onItemCheckedChange(item.cartItem, isChecked)
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else if (payloads.contains(QUANTITY_CHANGED_PAYLOAD) && holder is CartViewHolder) {
            val item = getItem(position) as CartListItem.CartData
            holder.bind(item.cartItem)
            holder.binding.quantitySelector.cartItem = item.cartItem
        }
    }

    companion object {
        const val QUANTITY_CHANGED_PAYLOAD = "quantity_changed"

        private const val TYPE_SKELETON = 0
        private const val TYPE_CART_ITEM = 1
    }
}
