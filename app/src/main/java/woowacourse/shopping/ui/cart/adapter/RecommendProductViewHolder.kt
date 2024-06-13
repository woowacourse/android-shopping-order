package woowacourse.shopping.ui.cart.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.listener.AddCartClickListener
import woowacourse.shopping.ui.listener.CountButtonClickListener
import woowacourse.shopping.ui.products.ProductWithQuantityUiModel

class RecommendProductViewHolder(
    private val binding: ItemProductBinding,
    private val countButtonClickListener: CountButtonClickListener,
    private val addCartClickListener: AddCartClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(productWithQuantity: ProductWithQuantityUiModel) {
        binding.productWithQuantity = productWithQuantity
        binding.countButtonClickListener = countButtonClickListener
        binding.addCartClickListener = addCartClickListener
    }
}
