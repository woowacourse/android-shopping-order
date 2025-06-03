package woowacourse.shopping.presentation.cart.event

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface CartEventHandler {
    fun onDeleteProduct(cartProduct: ProductUiModel)

    fun onNextPage()

    fun onPrevPage()

    fun hasNextPage(): Boolean

    fun hasPrevPage(): Boolean

    fun isPaginationEnabled(): Boolean

    fun getPage(): Int

    fun onCheckProduct(product: ProductUiModel)
}
