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
    ): List<Product> {
        val result = productService.getProducts(page = page, size = size)
        return result.products.map { it.toDomain() }
    }

    override suspend fun getRecentWatchingProducts(size: Int): List<Product> {
        val result = recentWatchingDao.getRecentWatchingProducts(size)
        return result.map { it.product }
    }

    override suspend fun getRecentRecommendWatchingProducts(size: Int): List<Product> {
        val result = recentWatchingDao.getRecentRecommendWatchingProducts(size)
        return result.map { it.product }
    }

    override suspend fun updateRecentWatchingProduct(product: Product) {
        recentWatchingDao.insertRecentWatching(
            RecentWatchingEntity(
                productId = product.id,
                product = product,
            ),
        )
    }

    override suspend fun getProduct(productId: Long): Product? {
        val result = productService.getProductDetail(productId)
        return result.toDomain()
    }

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: ProductsRepository? = null

        fun initialize(
            recentWatchingDao: RecentWatchingDao,
            productService: ProductService,
        ) {
            if (INSTANCE == null) {
                INSTANCE =
                    DefaultProductsRepository(
                        recentWatchingDao = recentWatchingDao,
                        productService = productService,
                    )
            }
        }

        fun get(): ProductsRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
