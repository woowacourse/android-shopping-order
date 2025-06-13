package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartRecommendationRepository {
    suspend fun getRecommendedProducts(): List<ProductUiModel>

    suspend fun getSelectedProductsCount(): Int

    suspend fun insertCartProduct(
        cartProduct: ProductUiModel,
    ): ProductUiModel

    suspend fun updateCartProduct(
        cartProduct: ProductUiModel,
        newCount: Int,
    ): Boolean

    suspend fun deleteCartProduct(
        cartProduct: ProductUiModel,
    ): Boolean
}
