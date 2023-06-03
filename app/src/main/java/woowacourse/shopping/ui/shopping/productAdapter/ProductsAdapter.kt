package woowacourse.shopping.ui.shopping.productAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.model.RecentProductUIModel
import woowacourse.shopping.ui.shopping.productAdapter.ProductsItemType.RecentProducts
import woowacourse.shopping.ui.shopping.productAdapter.viewHolder.ProductsViewHolder
import woowacourse.shopping.ui.shopping.productAdapter.viewHolder.ReadMoreViewHolder
import woowacourse.shopping.ui.shopping.productAdapter.viewHolder.RecentViewHolder
import woowacourse.shopping.ui.shopping.productAdapter.viewHolder.ShoppingViewHolder

class ProductsAdapter(
    private val listener: ProductsListener
) : RecyclerView.Adapter<ShoppingViewHolder>() {
    private val productItems: MutableList<ProductsItemType> = mutableListOf()
    private var cartCounts: Map<Int, Int> = emptyMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        return when (viewType) {
            ProductsItemType.TYPE_HEADER -> RecentViewHolder.from(parent, listener)
            ProductsItemType.TYPE_ITEM -> ProductsViewHolder.from(parent, listener)
            ProductsItemType.TYPE_FOOTER -> ReadMoreViewHolder.from(parent, listener)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        when (holder) {
            is RecentViewHolder -> holder.bind(productItems[position] as RecentProducts)
            is ProductsViewHolder -> holder.bind(productItems[position] as ProductsItemType.Product)
            is ReadMoreViewHolder -> Unit
        }
    }

    override fun getItemCount(): Int {
        return productItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return productItems[position].viewType
    }

    fun addList(products: List<ProductUIModel>) {
        productItems.removeIf { it is ProductsItemType.ReadMore }
        productItems.addAll(products.map { ProductsItemType.Product(it, getCount(it.id)) })
        productItems.add(ProductsItemType.ReadMore)
        notifyItemChanged(0)
    }

    fun updateRecentProducts(recentProducts: List<RecentProductUIModel>) {
        if (productItems.size > 0 && productItems[0] is RecentProducts) {
            productItems.removeAt(0)
        }

        if (recentProducts.isNotEmpty()) {
            productItems.add(0, RecentProducts(recentProducts))
        }

        if (productItems.size > 0) {
            notifyItemChanged(0)
        }
    }

    fun updateCartCounts(cartCounts: Map<Int, Int>) {
        this.cartCounts = cartCounts
        productItems.filterIsInstance<ProductsItemType.Product>()
            .forEach { it.count = cartCounts[it.product.id] ?: 0 }

        notifyItemRangeChanged(0, productItems.size - 1)
    }

    private fun getCount(productId: Int): Int {
        return cartCounts[productId] ?: 0
    }
}
