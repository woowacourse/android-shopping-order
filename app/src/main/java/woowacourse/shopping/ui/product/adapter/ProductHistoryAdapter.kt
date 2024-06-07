package woowacourse.shopping.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.common.OnProductItemClickListener
import woowacourse.shopping.databinding.HolderProductHistoryBinding
import woowacourse.shopping.domain.model.Product

class ProductHistoryAdapter(
    private val onProductItemClickListener: OnProductItemClickListener,
) : ListAdapter<Product, ProductHistoryViewHolder>(diffUtil) {

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
        holder.bind(getItem(position))
    }

    fun updateProductsHistory(newData: List<Product>) {
        submitList(newData)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}
