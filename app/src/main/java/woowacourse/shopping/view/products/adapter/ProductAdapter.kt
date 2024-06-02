package woowacourse.shopping.view.products.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.databinding.ItemProductSkeletonBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter
import woowacourse.shopping.view.products.OnClickProducts
import woowacourse.shopping.view.products.ShoppingItem
import woowacourse.shopping.view.products.adapter.viewholder.ProductViewHolder
import woowacourse.shopping.view.products.adapter.viewholder.SkeletonViewHolder

class ProductAdapter(
    private val onClickProducts: OnClickProducts,
    private val onClickCartItemCounter: OnClickCartItemCounter,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var products: MutableList<ShoppingItem> = mutableListOf()
    private var showSkeleton: Boolean = true
    private val productPosition: HashMap<Long, Int> = hashMapOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(addedProducts: List<Product>) {
        products.clear()
        products.addAll(addedProducts.map { ShoppingItem.ProductItem(it) })
        if (showSkeleton) {
            products.addAll(List(SKELETON_COUNT) { ShoppingItem.SkeletonItem })
        }
        notifyDataSetChanged()
    }

    fun updateProduct(productId: Long) {
        val position = productPosition[productId]
        if (position != null) {
            notifyItemChanged(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (products[position]) {
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
                    ItemProductSkeletonBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                SkeletonViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ProductViewHolder && position < products.size) {
            val item = (products[position] as ShoppingItem.ProductItem).product
            holder.bind(item)
            productPosition[item.id] = position
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setShowSkeleton(show: Boolean) {
        showSkeleton = show
        if (showSkeleton) {
            products.addAll(List(SKELETON_COUNT) { ShoppingItem.SkeletonItem })
        } else {
            products = products.filter { it !is ShoppingItem.SkeletonItem }.toMutableList()
        }
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_PRODUCT = 0
        private const val VIEW_TYPE_SKELETON = 1
        private const val SKELETON_COUNT = 10
    }
}
