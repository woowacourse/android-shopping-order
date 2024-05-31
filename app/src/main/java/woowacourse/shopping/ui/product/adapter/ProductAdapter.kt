package woowacourse.shopping.ui.product.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener

class ProductAdapter(
    private val onProductItemClickListener: OnProductItemClickListener,
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
) : RecyclerView.Adapter<ProductViewHolder>() {
    private var products: List<Product> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductViewHolder =
        ProductViewHolder(
            HolderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductItemClickListener,
            onItemQuantityChangeListener,
        )

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
    ) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    // TODO: 최적화
    @SuppressLint("NotifyDataSetChanged")
    fun updateAllLoadedProducts(newData: List<Product>) {
        this.products = newData
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "ProductRecyclerViewAdapter"
    }
}
