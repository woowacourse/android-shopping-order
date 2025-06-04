package woowacourse.shopping.presentation.view.cart.recommendation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener

class RecommendationAdapter(
    private val recommendEventListener: RecommendEventListener,
    private val itemCounterListener: ItemCounterListener,
) : ListAdapter<ProductUiModel, RecommendationViewHolder>(productDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendationViewHolder = RecommendationViewHolder.from(parent, recommendEventListener, itemCounterListener)

    override fun onBindViewHolder(
        holder: RecommendationViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateItem(updatedProduct: ProductUiModel) {
        val currentList = currentList.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedProduct.id }
        if (index != -1) {
            currentList[index] = updatedProduct
            submitList(currentList)
        }
    }

    companion object {
        private val productDiffCallback =
            object : DiffUtil.ItemCallback<ProductUiModel>() {
                override fun areItemsTheSame(
                    oldItem: ProductUiModel,
                    newItem: ProductUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ProductUiModel,
                    newItem: ProductUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}
