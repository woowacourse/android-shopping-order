package woowacourse.shopping.ui.productList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.HolderProductHistoryBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.OnProductItemClickListener

class ProductHistoryAdapter(
    private val onProductItemClickListener: OnProductItemClickListener,
) : ListAdapter<Product, ProductHistoryItemViewHolder>(productComparator) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductHistoryItemViewHolder =
        ProductHistoryItemViewHolder(
            HolderProductHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductItemClickListener,
        )

    override fun onBindViewHolder(
        holder: ProductHistoryItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val productComparator =
            object : DiffUtil.ItemCallback<Product>() {
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
    }
}