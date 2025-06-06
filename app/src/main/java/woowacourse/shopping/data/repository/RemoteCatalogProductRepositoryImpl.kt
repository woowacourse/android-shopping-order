package woowacourse.shopping.data.repository

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.CatalogRemoteDataSource
import woowacourse.shopping.data.dto.product.toUiModel
import woowacourse.shopping.product.catalog.ProductUiModel

class RemoteCatalogProductRepositoryImpl(
    private val remoteDataSource: CatalogRemoteDataSource,
) : CatalogProductRepository {
    override suspend fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
    ): List<ProductUiModel> =
        try {
            remoteDataSource
                .fetchProducts(
                    category = category,
                    page = page,
                    size = size,
                ).productContent
                .map { it.toUiModel() }
        } catch (e: Exception) {
            emptyList()
        }

    override suspend fun getAllProductsSize(): Long =
        try {
            remoteDataSource.fetchAllProducts().totalElements
        } catch (e: Exception) {
            0
        }

    override suspend fun getCartProductsByIds(productIds: List<Long>): List<ProductUiModel> {
        if (productIds.isEmpty()) return emptyList()

        return coroutineScope {
            val deferredProducts: List<Deferred<ProductUiModel?>> =
                productIds.map {
                    async {
                        getProduct(it)
                    }
                }

            deferredProducts
                .awaitAll()
                .filterNotNull()
                .sortedBy { (id, _) -> productIds.indexOf(id) }
        }
    }

    override suspend fun getProductsByPage(
        page: Int,
        size: Int,
    ): List<ProductUiModel> =
        try {
            remoteDataSource
                .fetchProducts(
                    category = null,
                    page = page,
                    size = size,
                ).productContent
                .map { it.toUiModel() }
        } catch (e: Exception) {
            emptyList()
        }

    override suspend fun getProduct(productId: Long): ProductUiModel? =
        try {
            remoteDataSource.fetchProductDetail(productId).toUiModel()
        } catch (e: Exception) {
            null
        }
}
