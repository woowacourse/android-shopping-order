package woowacourse.shopping.data.product.repository

import woowacourse.shopping.data.product.dataSource.ProductLocalDataSource
import woowacourse.shopping.data.product.dataSource.ProductRemoteDataSource
import woowacourse.shopping.data.product.local.entity.RecentWatchingEntity
import woowacourse.shopping.domain.product.Product
import kotlin.concurrent.thread

class DefaultProductsRepository(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productLocalDataSource: ProductLocalDataSource,
) : ProductsRepository {
    override fun getProducts(
        page: Int,
        size: Int,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        productRemoteDataSource.getProducts(page, size) { result ->
            val mappedResult =
                result.mapCatching { dto ->
                    dto?.toDomain() ?: emptyList()
                }
            onResult(mappedResult)
        }
    }

    override fun getRecentWatchingProducts(
        size: Int,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        thread {
            val result = productLocalDataSource.getRecentWatchingProducts(size)
            onResult(Result.success(result.map { it.product }))
        }
    }

    override fun getRecentRecommendWatchingProducts(
        size: Int,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        thread {
            val result = productLocalDataSource.getRecentRecommendWatchingProducts(size)
            onResult(Result.success(result.map { it.product }))
        }
    }

    override fun updateRecentWatchingProduct(
        product: Product,
        onResult: (Result<Unit>) -> Unit,
    ) {
        thread {
            productLocalDataSource.insertRecentWatching(
                RecentWatchingEntity(
                    productId = product.id,
                    product = product,
                ),
            )
            onResult(Result.success(Unit))
        }
    }

    override fun getProduct(
        productId: Long,
        onResult: (Result<Product?>) -> Unit,
    ) {
        productRemoteDataSource.getProductDetail(productId) { result ->
            val mappedResult =
                result.mapCatching { dto ->
                    dto?.toDomain()
                }
            onResult(mappedResult)
        }
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
