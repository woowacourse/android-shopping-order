package woowacourse.shopping.ui.order

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderProductBinding
import woowacourse.shopping.domain.model.Product

class RecommendProductViewHolder(
    private val binding: HolderProductBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {
        binding.product = product
    }
}
