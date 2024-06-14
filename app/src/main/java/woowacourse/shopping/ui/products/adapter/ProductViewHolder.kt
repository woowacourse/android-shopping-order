package woowacourse.shopping.ui.products.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel

class ProductViewHolder(
    private val binding: ItemProductBinding,
    private val viewModel: ProductContentsViewModel,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(productWithQuantity: ProductWithQuantityUiModel) {
        binding.productWithQuantity = productWithQuantity
        binding.countButtonClickListener = viewModel
        binding.addCartClickListener = viewModel
        binding.itemLayout.setOnClickListener {
            viewModel.itemClickListener(productWithQuantity.product.id)
        }
    }
}
