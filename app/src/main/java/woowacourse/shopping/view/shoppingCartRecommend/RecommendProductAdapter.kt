package woowacourse.shopping.view.shoppingCartRecommend

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.view.common.ProductQuantityClickListener
import woowacourse.shopping.view.product.ProductsItem

class RecommendProductAdapter(
    private val productListener: ProductQuantityClickListener,
) : RecyclerView.Adapter<RecommendProductViewHolder>() {
    private var recommendItems: List<ProductsItem.ProductItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductViewHolder = RecommendProductViewHolder.of(parent, productListener)

    override fun getItemCount(): Int = recommendItems.size

    override fun onBindViewHolder(
        holder: RecommendProductViewHolder,
        position: Int,
    ) {
        holder.bind(recommendItems[position])
    }

    fun submitList(productItems: List<ProductsItem.ProductItem>) {
        recommendItems = productItems
        notifyDataSetChanged()
    }
}
