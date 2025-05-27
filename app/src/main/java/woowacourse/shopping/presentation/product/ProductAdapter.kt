package woowacourse.shopping.presentation.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemLoadMoreBinding
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.cart.CartCounterClickListener

class ProductAdapter(
    private val onClickLoadMore: () -> Unit,
    private val cartCounterClickListener: CartCounterClickListener,
    private val itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<CartItem> = emptyList()
    private var showLoadMore: Boolean = false

    fun setData(
        newList: List<CartItem>,
        showLoadMore: Boolean,
    ) {
        this.items = newList
        this.showLoadMore = showLoadMore
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_product -> {
                val binding = ItemProductBinding.inflate(inflater, parent, false)
                ProductViewHolder(binding, cartCounterClickListener, itemClickListener)
            }

            R.layout.item_load_more -> {
                val binding = ItemLoadMoreBinding.inflate(inflater, parent, false)
                LoadMoreViewHolder(binding)
            }

            else -> throw IllegalArgumentException(ERROR_INVALID_VIEW_TYPE)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductViewHolder -> holder.bind(items[position])
            is LoadMoreViewHolder -> holder.bind(onClickLoadMore)
        }
    }

    override fun getItemCount(): Int = items.size + if (showLoadMore) 1 else 0

    override fun getItemViewType(position: Int): Int = if (position < items.size) R.layout.item_product else R.layout.item_load_more

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        cartCounterClickListener: CartCounterClickListener,
        itemClickListener: ItemClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemClickListener = itemClickListener
            binding.counterClickListener = cartCounterClickListener
        }

        fun bind(item: CartItem) {
            binding.cartItem = item
            binding.executePendingBindings()
        }
    }

    class LoadMoreViewHolder(
        private val binding: ItemLoadMoreBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onClickLoadMore: () -> Unit) {
            binding.onClickLoadMore = onClickLoadMore
            binding.executePendingBindings()
        }
    }

    companion object {
        private const val ERROR_INVALID_VIEW_TYPE = "Invalid view type"
    }
}
