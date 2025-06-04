package woowacourse.shopping.presentation.view.cart.recommendation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendedProductBinding
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener

class RecommendationViewHolder(
    private val binding: ItemRecommendedProductBinding,
    eventListener: RecommendEventListener,
    itemCounterListener: ItemCounterListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventListener = eventListener
        binding.recommendItemCounter.listener = itemCounterListener
    }

    fun bind(product: ProductUiModel) {
        binding.product = product
        binding.recommendItemCounter.amount = product.amount
        binding.recommendItemCounter.product = product
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventListener: RecommendEventListener,
            itemCounterListener: ItemCounterListener,
        ): RecommendationViewHolder {
            val binding =
                ItemRecommendedProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RecommendationViewHolder(binding, eventListener, itemCounterListener)
        }
    }
}
