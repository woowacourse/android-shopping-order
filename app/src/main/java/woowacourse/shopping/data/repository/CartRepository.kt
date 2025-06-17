package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartRepository {
    suspend fun getTotalProductsCount(): Int

    suspend fun updateCartProduct(
        cartProduct: ProductUiModel,
        newCount: Int,
    ): Boolean

    suspend fun deleteCartProduct(
        cartProduct: ProductUiModel,
    ): Boolean

    suspend fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
    ): List<ProductUiModel>
}
