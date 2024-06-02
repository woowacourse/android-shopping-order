package woowacourse.shopping.view.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.databinding.ItemCartPlaceholderBinding
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.cart.viewmodel.CartViewModel
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.ProductViewItem

class RecommendAdapter(
    private val viewModel: CartViewModel,
) : ListAdapter<ProductViewItem, RecyclerView.ViewHolder>(diffUtil) {
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
                LayoutInflater.from(parent.context),
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
                viewModel,
            )
        }
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<ProductViewItem>() {
                override fun areContentsTheSame(
                    oldItem: ProductViewItem,
                    newItem: ProductViewItem,
                ) = oldItem == newItem

                override fun areItemsTheSame(
                    oldItem: ProductViewItem,
                    newItem: ProductViewItem,
                ) = oldItem.product.productId == newItem.product.productId
            }
    }
}
