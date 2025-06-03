package woowacourse.shopping.presentation.view.cart.recommendation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toProductUiModel
import woowacourse.shopping.presentation.view.cart.recommendation.RecommendEventHandler
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler

class RecommendationAdapter(
    private val recommendEventHandler: RecommendEventHandler,
    private val itemCounterEventHandler: ItemCounterEventHandler,
) : ListAdapter<ProductUiModel, RecommendationViewHolder>(
        object : DiffUtil.ItemCallback<ProductUiModel>() {
            override fun areItemsTheSame(
                oldItem: ProductUiModel,
                newItem: ProductUiModel,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ProductUiModel,
                newItem: ProductUiModel,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendationViewHolder = RecommendationViewHolder.from(parent, recommendEventHandler, itemCounterEventHandler)

    override fun onBindViewHolder(
        holder: RecommendationViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateItem(updatedItem: CartItemUiModel) {
        val newList =
            currentList.map {
                if (it.id == updatedItem.cartItem.product.id) updatedItem.cartItem.toProductUiModel() else it
            }
        submitList(newList)
    }
}
