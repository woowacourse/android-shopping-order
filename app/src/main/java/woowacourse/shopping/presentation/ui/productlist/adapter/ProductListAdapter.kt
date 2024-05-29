package woowacourse.shopping.presentation.ui.productlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderLoadMoreBinding
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.productlist.PagingCart
import woowacourse.shopping.presentation.ui.productlist.ProductListActionHandler

class ProductListAdapter(
    private val actionHandler: ProductListActionHandler,
    private val productCountHandler: ProductCountHandler,
) : ListAdapter<Cart, ProductListAdapter.ProductListViewHolder>(CartDiffCallback) {
    private var isLast: Boolean = false

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) LOAD_VIEW_TYPE else PRODUCT_VIEW_TYPE
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductListViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            PRODUCT_VIEW_TYPE -> {
                ProductListViewHolder.ProductViewHolder(
                    HolderProductBinding.inflate(inflater, parent, false),
                    actionHandler,
                    productCountHandler,
                )
            }

            else -> {
                ProductListViewHolder.LoadMoreViewHolder(
                    HolderLoadMoreBinding.inflate(inflater, parent, false),
                    actionHandler,
                )
            }
        }
    }

    override fun getItemCount(): Int = if (currentList.size == 0) 0 else currentList.size + 1

    override fun onBindViewHolder(
        holder: ProductListViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductListViewHolder.ProductViewHolder -> holder.bind(getItem(position), position)
            is ProductListViewHolder.LoadMoreViewHolder -> holder.bind(isLast)
        }
    }

    fun updateProductList(newPagingCart: PagingCart) {
        isLast = newPagingCart.last
        if (isLast) notifyItemChanged(currentList.size)
        submitList(newPagingCart.cartList)
    }

    sealed class ProductListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class ProductViewHolder(
            private val binding: HolderProductBinding,
            private val actionHandler: ProductListActionHandler,
            private val productCountHandler: ProductCountHandler,
        ) : ProductListViewHolder(binding.root) {
            fun bind(
                cart: Cart,
                position: Int,
            ) {
                binding.cart = cart
                binding.position = position
                binding.actionHandler = actionHandler
                binding.productCountHandler = productCountHandler
                binding.executePendingBindings()
            }
        }

        class LoadMoreViewHolder(
            private val binding: HolderLoadMoreBinding,
            private val actionHandler: ProductListActionHandler,
        ) : ProductListViewHolder(binding.root) {
            fun bind(last: Boolean) {
                binding.last = last
                binding.actionHandler = actionHandler
                binding.executePendingBindings()
            }
        }
    }

    object CartDiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(
            oldItem: Cart,
            newItem: Cart,
        ): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(
            oldItem: Cart,
            newItem: Cart,
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val PRODUCT_VIEW_TYPE = 0
        const val LOAD_VIEW_TYPE = 1
    }
}
