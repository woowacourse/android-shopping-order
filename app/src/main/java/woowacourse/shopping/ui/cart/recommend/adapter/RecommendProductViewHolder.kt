package woowacourse.shopping.ui.cart.recommend.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiModel

class RecommendProductViewHolder(
    private val binding: ItemProductBinding,
    private val viewModel: CartViewModel,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(productWithQuantity: ProductWithQuantityUiModel) {
        binding.productWithQuantity = productWithQuantity
        binding.countButtonClickListener = viewModel
        binding.addCartClickListener = viewModel
    }
}
