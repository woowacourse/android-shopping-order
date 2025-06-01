package woowacourse.shopping.presentation.recommend

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class RecommendAdapter(
    private val onQuantityClick: (ProductUiModel) -> Unit,
) : RecyclerView.Adapter<RecommendViewHolder>() {
    private var items: List<ProductUiModel> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendViewHolder {
        return RecommendViewHolder(parent,onQuantityClick)
    }

    override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.count()

    fun setItems(newItems: List<ProductUiModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}