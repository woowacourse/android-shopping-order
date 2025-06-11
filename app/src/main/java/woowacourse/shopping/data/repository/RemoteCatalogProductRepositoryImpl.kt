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
    ): Result<List<ProductUiModel>> =
        runCatching {
            remoteDataSource
                .fetchProducts(
                    category = category,
                    page = page,
                    size = size,
                ).productContent
                .map { it.toUiModel() }
        }

    override suspend fun getAllProductsSize(): Result<Long> =
        runCatching {
            remoteDataSource.fetchAllProducts().totalElements
        }

    override suspend fun getCartProductsByIds(productIds: List<Long>): Result<List<ProductUiModel>> =
        runCatching {
            coroutineScope {
                val deferredProducts: List<Deferred<ProductUiModel?>> =
                    productIds.map { async { remoteDataSource.fetchProductDetail(it).toUiModel() } }

                deferredProducts
                    .awaitAll()
                    .filterNotNull()
                    .sortedBy { (id, _) -> productIds.indexOf(id) }
            }
        }

    override suspend fun getProductsByPage(
        page: Int,
        size: Int,
    ): Result<List<ProductUiModel>> =
        runCatching {
            remoteDataSource
                .fetchProducts(
                    category = null,
                    page = page,
                    size = size,
                ).productContent
                .map { it.toUiModel() }
        }

    override suspend fun getProduct(productId: Long): Result<ProductUiModel?> =
        runCatching {
            remoteDataSource.fetchProductDetail(productId).toUiModel()
        }
}
