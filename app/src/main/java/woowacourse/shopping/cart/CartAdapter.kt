package woowacourse.shopping.cart

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.cart.CartItem.ProductItem
import woowacourse.shopping.product.catalog.QuantityControlListener

class CartAdapter(
    private val quantityControlListener: QuantityControlListener,
    private val cartActionListener: CartActionListener,
) : ListAdapter<ProductItem, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<ProductItem>() {
            override fun areItemsTheSame(
                oldItem: ProductItem,
                newItem: ProductItem,
            ): Boolean = oldItem.productItem.id == newItem.productItem.id

            override fun areContentsTheSame(
                oldItem: ProductItem,
                newItem: ProductItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder = CartViewHolder.from(parent, cartActionListener, quantityControlListener)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is CartViewHolder -> holder.bind((currentList[position] as ProductItem).productItem)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (currentList[position]) {
            is ProductItem -> CART_PRODUCT
        }

    companion object {
        private const val CART_PRODUCT = 1
    }
}
