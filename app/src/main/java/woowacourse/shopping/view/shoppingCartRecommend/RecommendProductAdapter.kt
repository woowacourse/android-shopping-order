package woowacourse.shopping.view.shoppingCartRecommend

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.view.common.ProductQuantityClickListener
import woowacourse.shopping.view.product.ProductsItem

class RecommendProductAdapter(
    private val productListener: ProductQuantityClickListener,
) : ListAdapter<ProductsItem.ProductItem, RecommendProductViewHolder>(
        object : DiffUtil.ItemCallback<ProductsItem.ProductItem>() {
            override fun areItemsTheSame(
                oldItem: ProductsItem.ProductItem,
                newItem: ProductsItem.ProductItem,
            ): Boolean = oldItem.product.id == newItem.product.id

            override fun areContentsTheSame(
                oldItem: ProductsItem.ProductItem,
                newItem: ProductsItem.ProductItem,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductViewHolder = RecommendProductViewHolder.of(parent, productListener)

    override fun onBindViewHolder(
        holder: RecommendProductViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
