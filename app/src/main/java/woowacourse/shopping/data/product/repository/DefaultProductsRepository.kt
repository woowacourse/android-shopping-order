package woowacourse.shopping.data.product.repository

import woowacourse.shopping.data.product.dataSource.ProductLocalDataSource
import woowacourse.shopping.data.product.dataSource.ProductRemoteDataSource
import woowacourse.shopping.data.product.local.entity.RecentWatchingEntity
import woowacourse.shopping.domain.product.Product

class DefaultProductsRepository(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productLocalDataSource: ProductLocalDataSource,
) : ProductsRepository {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>> =
        runCatching {
            productRemoteDataSource.getProducts(page, size).toDomain()
        }

    override suspend fun getRecentWatchingProducts(size: Int): Result<List<Product>> =
        runCatching {
            productLocalDataSource.getRecentWatchingProducts(size).map { it.product }
        }

    override suspend fun getRecentRecommendWatchingProducts(size: Int): Result<List<Product>> =
        runCatching {
            productLocalDataSource.getRecentRecommendWatchingProducts(size).map { it.product }
        }

    override suspend fun updateRecentWatchingProduct(product: Product): Result<Unit> =
        runCatching {
            productLocalDataSource.insertRecentWatching(
                RecentWatchingEntity(
                    productId = product.id,
                    product = product,
                ),
            )
        }

    override suspend fun getProduct(productId: Long): Result<Product?> =
        runCatching {
            productRemoteDataSource.getProductDetail(productId).toDomain()
        }

    companion object {
        private var instance: ProductsRepository? = null

        fun initialize(
            productRemoteDataSource: ProductRemoteDataSource,
            productLocalDataSource: ProductLocalDataSource,
        ) {
            if (instance == null) {
                instance =
                    DefaultProductsRepository(
                        productRemoteDataSource = productRemoteDataSource,
                        productLocalDataSource = productLocalDataSource,
                    )
            }
        }

        fun get(): ProductsRepository = instance ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
