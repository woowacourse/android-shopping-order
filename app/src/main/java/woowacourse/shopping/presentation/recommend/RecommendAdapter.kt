package woowacourse.shopping.presentation.recommend

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.product.catalog.event.CatalogEventHandler

class RecommendAdapter(
    private val catalogEventHandler: CatalogEventHandler,
    private val quantityHandler: ProductQuantityHandler,
) : RecyclerView.Adapter<RecommendViewHolder>() {
    private var items: List<ProductUiModel> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        return RecommendViewHolder(parent, catalogEventHandler, quantityHandler)
    }

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.count()

    fun setItems(newItems: List<ProductUiModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
