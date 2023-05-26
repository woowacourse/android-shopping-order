package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.ui.productdetail.uistate.LastViewedProductUIState

class LastViewedProductViewHolder(
    private val binding: ActivityProductDetailBinding,
    private val onClick: (Long) -> Unit
) {
    init {
        binding.layoutLastViewedProduct.setOnClickListener {
            onClick(binding.lastViewedProduct?.id ?: return@setOnClickListener)
        }
    }

    fun bind(lastViewedProduct: LastViewedProductUIState?) {
        binding.lastViewedProduct = lastViewedProduct
    }
}
