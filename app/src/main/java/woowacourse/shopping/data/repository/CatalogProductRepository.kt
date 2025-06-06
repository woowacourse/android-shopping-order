package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CatalogProductRepository {
    suspend fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
    ): List<ProductUiModel>

    suspend fun getAllProductsSize(): Long

    suspend fun getCartProductsByIds(productIds: List<Long>): List<ProductUiModel>

    suspend fun getProductsByPage(
        page: Int,
        size: Int,
    ): List<ProductUiModel>

    suspend fun getProduct(productId: Long): ProductUiModel?
}
