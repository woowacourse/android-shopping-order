package woowacourse.shopping.presentation.ui.cart.fragment

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.presentation.ui.QuantityHandler

class RecommendViewHolder(
    private val binding: ItemRecommendBinding,
    private val quantityHandler: QuantityHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ProductListItem.ShoppingProductItem) {
        binding.product = item
        binding.quantityHandler = quantityHandler
    }
}
