package woowacourse.shopping.presentation.cart.event

import woowacourse.shopping.presentation.cart.CartViewModel
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartEventHandlerImpl(
    private val viewModel: CartViewModel,
) : CartEventHandler,
    ProductQuantityHandler {
    override fun onDeleteProduct(cartProduct: ProductUiModel) =
        viewModel.onDeleteProduct(cartProduct)

    override fun onNextPage() = viewModel.onNextPage()

    override fun onPrevPage() = viewModel.onPrevPage()

    override fun isNextButtonEnabled() = viewModel.isNextButtonEnabled()

    override fun isPrevButtonEnabled() = viewModel.isPrevButtonEnabled()

    override fun isPaginationEnabled(): Boolean = viewModel.isPaginationEnabled()

    override fun getPage(): Int = viewModel.getPage()

    override fun onCheckProduct(product: ProductUiModel) = viewModel.onCheckProduct(product)

    override fun onPlusQuantity(product: ProductUiModel) = viewModel.increaseQuantity(product)

    override fun onMinusQuantity(product: ProductUiModel) = viewModel.decreaseQuantity(product)
}
