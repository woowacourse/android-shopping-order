package woowacourse.shopping.presentation.cart.event

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface CartEventHandler {
    fun onDeleteProduct(cartProduct: ProductUiModel)

    fun onNextPage()

    fun onPrevPage()

    fun isNextButtonEnabled(): Boolean

    fun isPrevButtonEnabled(): Boolean

    fun isPaginationEnabled(): Boolean

    fun getPage(): Int
}
