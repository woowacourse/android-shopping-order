package woowacourse.shopping.presentation.ui.cart.fragment

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemRecommendBinding
import woowacourse.shopping.presentation.ui.cart.CartHandler
import woowacourse.shopping.presentation.ui.model.ProductModel

class RecommendViewHolder(
    private val binding: ItemRecommendBinding,
    private val cartHandler: CartHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ProductModel) {
        binding.product = item
        binding.cartHandler = cartHandler
    }
}
