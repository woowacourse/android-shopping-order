package woowacourse.shopping.view.recommend

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.view.product.ProductItemActions

class RecommendProductsAdapter(
    private val productItemActions: ProductItemActions,
) : RecyclerView.Adapter<RecommendProductsViewHolder>() {
    private val items: List<RecommendProduct> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendProductsViewHolder =
        RecommendProductsViewHolder.of(
            parent = parent,
            productItemActions = productItemActions,
        )

    override fun onBindViewHolder(
        holder: RecommendProductsViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
