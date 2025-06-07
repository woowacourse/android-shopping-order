package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartRepository {
    fun getTotalProductsCount(callback: (Int) -> Unit)

    fun updateCartProduct(
        cartProduct: ProductUiModel,
        newCount: Int,
        callback: (Boolean) -> Unit,
    )

    fun deleteCartProduct(
        cartProduct: ProductUiModel,
        callback: (Boolean) -> Unit,
    )

    fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )
}
