package woowacourse.shopping.view.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendProductBinding
import woowacourse.shopping.view.product.ProductItemActions

class RecommendProductsViewHolder(
    private val binding: ItemRecommendProductBinding,
    productItemActions: ProductItemActions,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.productItemActionListener = productItemActions
    }

    fun bind(item: RecommendProduct) {
        binding.recommendProduct = item
    }

    companion object {
        fun of(
            parent: ViewGroup,
            productItemActions: ProductItemActions,
        ): RecommendProductsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecommendProductBinding.inflate(layoutInflater, parent, false)
            return RecommendProductsViewHolder(binding, productItemActions)
        }
    }
}
