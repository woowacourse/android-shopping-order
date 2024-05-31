package woowacourse.shopping.ui.product.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductHistoryBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.OnProductItemClickListener

class ProductHistoryAdapter(
    private val onProductItemClickListener: OnProductItemClickListener,
) : RecyclerView.Adapter<ProductHistoryViewHolder>() {
    private var products: List<Product> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductHistoryViewHolder =
        ProductHistoryViewHolder(
            HolderProductHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductItemClickListener,
        )

    override fun onBindViewHolder(
        holder: ProductHistoryViewHolder,
        position: Int,
    ) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    // TODO: 최적화
    @SuppressLint("NotifyDataSetChanged")
    fun update(newData: List<Product>) {
        this.products = newData.reversed()
        notifyDataSetChanged()
    }
}
