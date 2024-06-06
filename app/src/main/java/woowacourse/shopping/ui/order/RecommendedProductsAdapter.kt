package woowacourse.shopping.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.ui.OnItemQuantityChangeListener

class RecommendedProductsAdapter(
    private val onItemQuantityChangeListener: OnItemQuantityChangeListener,
) : ListAdapter<Product, RecommendedProductViewHolder>(productComparator) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendedProductViewHolder =
        RecommendedProductViewHolder(
            HolderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemQuantityChangeListener,
        )

    override fun onBindViewHolder(
        holder: RecommendedProductViewHolder,
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
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Product,
                    newItem: Product,
                ): Boolean = oldItem == newItem
            }
    }
}
