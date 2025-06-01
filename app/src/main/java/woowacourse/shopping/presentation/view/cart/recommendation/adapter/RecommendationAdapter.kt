package woowacourse.shopping.presentation.view.cart.recommendation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.recommendation.RecommendEventListener

class RecommendationAdapter(
    recommendedProducts: List<ProductUiModel> = emptyList(),
    private val recommendEventListener: RecommendEventListener,
    private val itemCounterListener: ItemCounterListener,
) : RecyclerView.Adapter<RecommendationViewHolder>() {
    private val recommendedProducts = recommendedProducts.toMutableList()

    override fun getItemCount(): Int = recommendedProducts.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendationViewHolder = RecommendationViewHolder.from(parent, recommendEventListener, itemCounterListener)

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

    fun updateItem(updatedItem: CartItemUiModel) {
        val index =
            recommendedProducts.indexOfFirst { it.id == updatedItem.cartItem.product.id }
        if (index != -1) {
            recommendedProducts[index] = updatedItem.cartItem.toProductUiModel()
            notifyItemChanged(index)
        }
    }
}
