package woowacourse.shopping.data.product.repository

import woowacourse.shopping.data.product.local.dao.RecentWatchingDao
import woowacourse.shopping.data.product.local.entity.RecentWatchingEntity
import woowacourse.shopping.data.product.remote.service.ProductService
import woowacourse.shopping.domain.product.Product

class DefaultProductsRepository(
    private val productService: ProductService,
    private val recentWatchingDao: RecentWatchingDao,
) : ProductsRepository {
    override suspend fun load(
        page: Int,
        size: Int,
    ): Result<List<Product>> {
        val result = productService.getProducts(page = page, size = size)
        return runCatching {
            result.products.map { it.toDomain() }
        }
    }

    override suspend fun getRecentWatchingProducts(size: Int): Result<List<Product>> {
        val result = recentWatchingDao.getRecentWatchingProducts(size)
        return runCatching {
            result.map { it.product }
        }
    }

    override suspend fun getRecentRecommendWatchingProducts(size: Int): Result<List<Product>> {
        val result = recentWatchingDao.getRecentRecommendWatchingProducts(size)
        return runCatching {
            result.map { it.product }
        }
    }

    override suspend fun updateRecentWatchingProduct(product: Product): Result<Unit> {
        return runCatching {
            recentWatchingDao.insertRecentWatching(
                RecentWatchingEntity(
                    productId = product.id,
                    product = product,
                ),
            )
        }
    }

    override suspend fun getProduct(productId: Long): Result<Product?> {
        val result = productService.getProductDetail(productId)
        return runCatching {
            result.toDomain()
        }
    }

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: ProductsRepository? = null

        fun initialize(
            recentWatchingDao: RecentWatchingDao,
            productService: ProductService,
        ) {
            INSTANCE =
                DefaultProductsRepository(
                    recentWatchingDao = recentWatchingDao,
                    productService = productService,
                )
        }

        fun get(): ProductsRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
