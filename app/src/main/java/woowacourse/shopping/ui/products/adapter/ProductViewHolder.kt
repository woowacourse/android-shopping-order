package woowacourse.shopping.ui.products.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.products.ProductItemClickListener
import woowacourse.shopping.ui.products.ProductWithQuantityUiModel
import woowacourse.shopping.ui.utils.AddCartClickListener

class ProductViewHolder(
    private val binding: ItemProductBinding,
    private val countButtonClickListener: CountButtonClickListener,
    private val addCartClickListener: AddCartClickListener,
    private val productItemClickListener: ProductItemClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(productWithQuantity: ProductWithQuantityUiModel) {
        binding.productWithQuantity = productWithQuantity
        binding.countButtonClickListener = countButtonClickListener
        binding.addCartClickListener = addCartClickListener
        binding.productItemClickListener = productItemClickListener
    }
}
