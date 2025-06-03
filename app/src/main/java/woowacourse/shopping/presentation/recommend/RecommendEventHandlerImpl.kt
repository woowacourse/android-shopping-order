package woowacourse.shopping.presentation.recommend

import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class RecommendEventHandlerImpl(
    private val viewModel: RecommendViewModel,
) : ProductQuantityHandler {
    override fun onPlusQuantity(product: ProductUiModel) {
        viewModel.increaseQuantity(product)
    }

    override fun onMinusQuantity(product: ProductUiModel) {
        viewModel.decreaseQuantity(product)
    }
}
