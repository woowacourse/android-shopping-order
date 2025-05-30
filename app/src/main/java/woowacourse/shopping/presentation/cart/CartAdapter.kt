package woowacourse.shopping.presentation.cart

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.presentation.cart.event.CartEventHandler
import woowacourse.shopping.presentation.cart.viewHolder.CartViewHolder
import woowacourse.shopping.presentation.cart.viewHolder.PaginationButtonViewHolder
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartAdapter(
    private var cartProducts: List<ProductUiModel>,
    private val cartHandler: CartEventHandler,
    private val handler: ProductQuantityHandler,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var pagingData: PagingData? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_CART_PRODUCT) {
            CartViewHolder.from(parent, cartHandler, handler)
        } else {
            PaginationButtonViewHolder.from(parent)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is CartViewHolder -> holder.bind(cartProducts[position])
            is PaginationButtonViewHolder ->
                holder.bind(cartHandler)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == cartProducts.size) {
            return VIEW_TYPE_PAGINATION_BUTTON
        }
        return VIEW_TYPE_CART_PRODUCT
    }

    fun setData(
        newCartProducts: List<ProductUiModel>,
        pagingData: PagingData,
    ) {
        val hadPagination = shouldShowPagination()
        val oldSize = cartProducts.size
        val oldTotalCount = oldSize + if (hadPagination) 1 else 0

        val newSize = newCartProducts.size
        val newTotalCount = newSize + if (shouldShowPagination()) 1 else 0

        this.cartProducts = newCartProducts
        this.pagingData = pagingData

        if (oldTotalCount != newTotalCount) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeChanged(0, newTotalCount)
        }
    }

    private fun shouldShowPagination(): Boolean = pagingData?.hasPrevious == true || pagingData?.hasNext == true

    override fun getItemCount(): Int = cartProducts.size + if (shouldShowPagination()) 1 else 0

    fun updateProduct(product: ProductUiModel) {
        val index = cartProducts.indexOfFirst { it.id == product.id }
        if (index != -1) {
            cartProducts =
                cartProducts.toMutableList().apply {
                    set(index, product)
                }
            notifyItemChanged(index)
        }
    }

    fun setPagination() {
        val paginationPos = itemCount - 1
        notifyItemChanged(paginationPos)
    }

    companion object {
        private val VIEW_TYPE_CART_PRODUCT = R.layout.product_item
        private val VIEW_TYPE_PAGINATION_BUTTON = R.layout.pagination_button_item
    }
}
