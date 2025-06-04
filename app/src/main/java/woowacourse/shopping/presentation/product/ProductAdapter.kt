package woowacourse.shopping.presentation.product

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.presentation.cart.CartCounterClickListener

class ProductAdapter(
    private val onClickLoadMore: () -> Unit,
    private val cartCounterClickListener: CartCounterClickListener,
    private val itemClickListener: ItemClickListener,
) : ListAdapter<ProductItemType, RecyclerView.ViewHolder>(ProductDiffUtil) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is ProductItemType.Product -> R.layout.item_product
            is ProductItemType.LoadMore -> R.layout.item_load_more
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.item_product ->
                ProductViewHolder.create(parent, cartCounterClickListener, itemClickListener)

            R.layout.item_load_more -> LoadMoreViewHolder.create(parent)
            else -> throw IllegalArgumentException(ERROR_INVALID_VIEW_TYPE)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductViewHolder -> holder.bind(getItem(position) as ProductItemType.Product)
            is LoadMoreViewHolder -> holder.bind(onClickLoadMore)
        }
    }

    companion object {
        private const val ERROR_INVALID_VIEW_TYPE = "Invalid view type"
    }
}
