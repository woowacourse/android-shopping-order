package woowacourse.shopping.presentation.view.cart.recommendation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendedProductBinding
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler

class RecommendationViewHolder(
    private val binding: ItemRecommendedProductBinding,
    itemCounterEventHandler: ItemCounterEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventHandler = itemCounterEventHandler
        binding.recommendItemCounter.eventHandler = itemCounterEventHandler
    }

    fun bind(product: ProductUiModel) {
        binding.product = product
    }

    companion object {
        fun from(
            parent: ViewGroup,
            itemCounterEventHandler: ItemCounterEventHandler,
        ): RecommendationViewHolder {
            val binding =
                ItemRecommendedProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RecommendationViewHolder(binding, itemCounterEventHandler)
        }
    }
}
