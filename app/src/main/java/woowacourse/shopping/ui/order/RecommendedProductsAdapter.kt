package woowacourse.shopping.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product

class RecommendedProductsAdapter(
    // TODO: listener
) : ListAdapter<Product, RecommendedProductViewHolder>(productComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedProductViewHolder =
        RecommendedProductViewHolder(
            HolderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: RecommendedProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val productComparator =
            object : DiffUtil.ItemCallback<Product>() {
                override fun areItemsTheSame(
                    oldItem: Product,
                    newItem: Product,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Product,
                    newItem: Product,
                ): Boolean = oldItem == newItem
            }
    }
}
