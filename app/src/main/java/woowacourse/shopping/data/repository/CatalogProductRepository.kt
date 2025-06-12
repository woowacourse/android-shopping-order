package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CatalogProductRepository {
    suspend fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<ProductUiModel>>

    suspend fun getAllProductsSize(): Result<Long>

    suspend fun getCartProductsByIds(productIds: List<Long>): Result<List<ProductUiModel>>

    suspend fun getProductsByPage(
        page: Int,
        size: Int,
    ): Result<List<ProductUiModel>>

    suspend fun getProduct(productId: Long): Result<ProductUiModel?>
}
