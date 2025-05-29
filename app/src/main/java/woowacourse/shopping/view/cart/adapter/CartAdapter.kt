package woowacourse.shopping.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.view.cart.state.CartState
import woowacourse.shopping.view.core.handler.CartQuantityHandler

class CartAdapter(
    private val handler: Handler,
    private val cartQuantityHandler: CartQuantityHandler,
) : RecyclerView.Adapter<CartViewHolder>() {
    private var itemCache: List<CartState> = emptyList()
    private var visibleItems: List<CartState> = emptyList()

    fun submitList(
        newItems: List<CartState>,
        page: Int,
    ) {
        itemCache = newItems
        visibleItems = getCurrentPageItems(page)
        notifyDataSetChanged()
    }

    private fun getCurrentPageItems(page: Int): List<CartState> {
        val fromIndex = (page - 1) * PAGE_SIZE
        val toIndex = (fromIndex + PAGE_SIZE).coerceAtMost(itemCache.size)
        return if (fromIndex in 0 until toIndex) {
            itemCache.subList(fromIndex, toIndex)
        } else {
            emptyList()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding, handler, cartQuantityHandler)
    }

    override fun getItemCount(): Int = visibleItems.size

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
    ) {
        holder.bind(visibleItems[position])
    }

    interface Handler : CartViewHolder.Handler

    companion object {
        private const val PAGE_SIZE = 5
    }
}
