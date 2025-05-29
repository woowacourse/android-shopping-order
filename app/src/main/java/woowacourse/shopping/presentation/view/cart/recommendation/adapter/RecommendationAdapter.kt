package woowacourse.shopping.presentation.view.cart.recommendation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener

class RecommendationAdapter(
    recommendedProducts: List<ProductUiModel> = emptyList(),
    private val itemCounterListener: ItemCounterListener,
) : RecyclerView.Adapter<RecommendationViewHolder>() {
    private val recommendedProducts = recommendedProducts.toMutableList()

    override fun getItemCount(): Int = recommendedProducts.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendationViewHolder = RecommendationViewHolder.from(parent, itemCounterListener)

    override fun onBindViewHolder(
        holder: RecommendationViewHolder,
        position: Int,
    ) {
        holder.bind(recommendedProducts[position])
    }

    fun updateRecommendedProducts(recommendedProducts: List<ProductUiModel>) {
        this.recommendedProducts.clear()
        this.recommendedProducts.addAll(recommendedProducts)
        notifyDataSetChanged()
    }

    fun updateItem(updatedItem: ProductUiModel) {
        val index = recommendedProducts.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            recommendedProducts[index] = updatedItem
            notifyItemChanged(index)
        }
    }
}
