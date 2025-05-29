package woowacourse.shopping.presentation.view.cart.recommendation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.recommendation.RecommendEventListener

class RecommendationAdapter(
    recommendedProducts: List<ProductUiModel> = emptyList(),
    private val recommendEventListener: RecommendEventListener,
    private val itemCounterListener: ItemCounterListener,
) : RecyclerView.Adapter<RecommendationViewHolder>() {
    private val items = mutableListOf<ProductUiModel>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendationViewHolder = RecommendationViewHolder.from(parent, recommendEventListener, itemCounterListener)

    override fun onBindViewHolder(
        holder: RecommendationViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    fun updateRecommendedProducts(newProducts: List<ProductUiModel>) {
        items.clear()
        items.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun updateItem(updatedProduct: ProductUiModel) {
        val index = items.indexOfFirst { it.id == updatedProduct.id }
        if (index != -1) {
            items[index] = updatedProduct
            notifyItemChanged(index)
        } else {
        }
    }
}
