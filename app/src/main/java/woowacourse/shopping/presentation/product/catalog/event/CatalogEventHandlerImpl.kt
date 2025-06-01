package woowacourse.shopping.presentation.product.catalog.event

import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.CatalogViewModel
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CatalogEventHandlerImpl(
    private val viewModel: CatalogViewModel,
    private val onNavigateToDetail: (ProductUiModel) -> Unit,
) : CatalogEventHandler,
    ProductQuantityHandler {
    override fun onProductClick(product: ProductUiModel) {
        onNavigateToDetail(product)
    }

    override fun onLoadButtonClick() {
        viewModel.loadNextCatalogProducts()
    }

    override fun toggleQuantity(product: ProductUiModel) {
        viewModel.toggleQuantity(product)
    }

    override fun onPlusQuantity(product: ProductUiModel) {
        viewModel.increaseQuantity(product)
    }

    override fun onMinusQuantity(product: ProductUiModel) {
        viewModel.decreaseQuantity(product)
    }
}
