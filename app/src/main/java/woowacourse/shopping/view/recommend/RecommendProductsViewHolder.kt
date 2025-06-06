package woowacourse.shopping.view.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendProductBinding

class RecommendProductsViewHolder(
    private val binding: ItemRecommendProductBinding,
    recommendProductItemActions: RecommendProductItemActions,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.recommendProductItemActions = recommendProductItemActions
    }

    fun bind(item: RecommendProduct) {
        binding.recommendProduct = item
    }

    companion object {
        fun of(
            parent: ViewGroup,
            recommendProductItemActions: RecommendProductItemActions,
        ): RecommendProductsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecommendProductBinding.inflate(layoutInflater, parent, false)
            return RecommendProductsViewHolder(binding, recommendProductItemActions)
        }
    }
}
