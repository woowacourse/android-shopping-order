package woowacourse.shopping.data.source

import woowacourse.shopping.product.catalog.ProductUiModel

interface CatalogProductDataSource {
    suspend fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
    ): List<ProductUiModel>

    suspend fun getAllProductsSize(): Int

    suspend fun getCartProductsByUids(
        uids: List<Int>,
    ): List<ProductUiModel>

    suspend fun getProductsByPage(
        page: Int,
        size: Int,
    ): List<ProductUiModel>

    suspend fun getProduct(
        id: Int,
    ): ProductUiModel
}
