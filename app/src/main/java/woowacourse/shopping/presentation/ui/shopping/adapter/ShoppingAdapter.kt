package woowacourse.shopping.presentation.ui.shopping.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.ui.counter.CounterHandler
import woowacourse.shopping.presentation.ui.shopping.ShoppingEventHandler
import woowacourse.shopping.presentation.ui.shopping.adapter.viewholder.ShoppingViewHolder

class ShoppingAdapter(
    private val shoppingEventHandler: ShoppingEventHandler,
    private val shoppingItemCountHandler: CounterHandler,
) : ListAdapter<ShoppingProduct, ShoppingViewHolder>(diffCallback) {
    private var products: List<Product> = emptyList()
    private var shoppingProducts: List<ShoppingProduct> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShoppingViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ShoppingViewHolder,
        position: Int,
    ) {
        val shoppingProduct = shoppingProducts[position]
        return holder.bind(shoppingProduct, shoppingEventHandler, shoppingItemCountHandler)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun loadData(products: List<Product>) {
        this.products = products
    }

    fun loadShoppingProductData(shoppingProducts: List<ShoppingProduct>) {
        this.shoppingProducts = shoppingProducts
    }

    companion object {
        val diffCallback =
            object : DiffUtil.ItemCallback<ShoppingProduct>() {
                override fun areItemsTheSame(
                    oldItem: ShoppingProduct,
                    newItem: ShoppingProduct,
                ): Boolean {
                    return oldItem.product.id == newItem.product.id
                }

                override fun areContentsTheSame(
                    oldItem: ShoppingProduct,
                    newItem: ShoppingProduct,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
