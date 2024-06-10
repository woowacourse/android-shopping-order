package woowacourse.shopping.view.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter
import woowacourse.shopping.view.products.OnClickProducts
import woowacourse.shopping.view.products.adapter.viewholder.ProductViewHolder

class ProductAdapter(
    private val onClickProducts: OnClickProducts,
    private val onClickCartItemCounter: OnClickCartItemCounter,
) : ListAdapter<Product, ProductViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product,
        ): Boolean {
            return oldItem == newItem
        }
    }

    fun updateProducts(addedProducts: List<Product>) {
        submitList(addedProducts)
    }

    fun updateProduct(productId: Long) {
        val position =
            currentList.indexOfFirst { it.id == productId }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductViewHolder {
        val view =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(view, onClickCartItemCounter, onClickProducts)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }
}
