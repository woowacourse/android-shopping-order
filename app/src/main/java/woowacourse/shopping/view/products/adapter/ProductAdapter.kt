package woowacourse.shopping.view.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemProductSkeletonBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter
import woowacourse.shopping.view.products.OnClickProducts
import woowacourse.shopping.view.products.adapter.viewholder.ProductViewHolder
import woowacourse.shopping.view.products.adapter.viewholder.SkeletonViewHolder
import woowacourse.shopping.view.products.model.ShoppingItem

class ProductAdapter(
    private val onClickProducts: OnClickProducts,
    private val onClickCartItemCounter: OnClickCartItemCounter,
) : ListAdapter<ShoppingItem, RecyclerView.ViewHolder>(DiffCallback()) {
    private var showSkeleton: Boolean = true

    class DiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(
            oldItem: ShoppingItem,
            newItem: ShoppingItem,
        ): Boolean {
            return if (oldItem is ShoppingItem.ProductItem && newItem is ShoppingItem.ProductItem) {
                oldItem.product.id == newItem.product.id
            } else {
                oldItem == newItem
            }
        }

        override fun areContentsTheSame(
            oldItem: ShoppingItem,
            newItem: ShoppingItem,
        ): Boolean {
            return oldItem == newItem
        }
    }

    fun updateProducts(addedProducts: List<Product>) {
        submitList(addedProducts.map { ShoppingItem.ProductItem(it) })
    }

    fun updateProduct(productId: Long) {
        val position =
            currentList.indexOfFirst {
                it is ShoppingItem.ProductItem && it.product.id == productId
            }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ShoppingItem.ProductItem -> VIEW_TYPE_PRODUCT
            else -> VIEW_TYPE_SKELETON
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PRODUCT -> {
                val view =
                    ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProductViewHolder(view, onClickCartItemCounter, onClickProducts)
            }

            else -> {
                val view =
                    ItemProductSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SkeletonViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ProductViewHolder) {
            val item = (getItem(position) as ShoppingItem.ProductItem).product
            holder.bind(item)
        }
    }

    fun setShowSkeleton(show: Boolean) {
        showSkeleton = show
        val currentListWithoutSkeletons = currentList.filter { it !is ShoppingItem.SkeletonItem }
        if (showSkeleton) {
            val newList = currentListWithoutSkeletons.toMutableList()
            newList.addAll(List(SKELETON_COUNT) { ShoppingItem.SkeletonItem })
            submitList(newList)
        } else {
            submitList(currentListWithoutSkeletons)
        }
    }

    companion object {
        private const val VIEW_TYPE_PRODUCT = 0
        private const val VIEW_TYPE_SKELETON = 1
        private const val SKELETON_COUNT = 10
    }
}
