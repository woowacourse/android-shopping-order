package woowacourse.shopping.data

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.db.RecentProductDao
import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.mapper.toRecentEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val recentProductDao: RecentProductDao,
    private val productDataSource: ProductDataSource,
) : ProductRepository {
    override suspend fun loadProducts(
        page: Int,
        loadSize: Int,
    ): Result<Pair<List<Product>, Boolean>> =
        runCatching {
            val result =
                productDataSource
                    .fetchProducts(
                        page = page,
                        size = loadSize,
                    ).getOrThrow()

            val products = result.productContent.map { it.toProduct() }
            val hasMore = products.size >= loadSize

            products to hasMore
        }

    override suspend fun getProductById(id: Long): Result<Product> =
        runCatching {
            productDataSource.fetchProduct(id).getOrThrow().toProduct()
        }

    override suspend fun loadProductsByCategory(category: String): Result<List<Product>> =
        runCatching {
            val response =
                productDataSource
                    .fetchProducts(
                        page = 0,
                        size = Int.MAX_VALUE,
                    ).getOrThrow()

            response.productContent
                .map { it.toProduct() }
                .filter { it.category == category }
        }

    override suspend fun addRecentProduct(product: Product): Result<Product> =
        runCatching {
            recentProductDao.insert(product.toRecentEntity())
            product
        }

    override suspend fun loadRecentProducts(limit: Int): Result<List<Product>> =
        runCatching {
            recentProductDao.getRecentProducts(limit).map { it.toProduct() }
        }

    override suspend fun loadLastViewedProduct(currentProductId: Long): Result<Product> =
        runCatching {
            recentProductDao.getLastViewedProduct(currentProductId)?.toProduct()
                ?: throw NoSuchElementException("최근 본 상품이 없습니다.")
        }

    override suspend fun getMostRecentProduct(): Result<Product> =
        runCatching {
            recentProductDao.getMostRecentProduct()?.toProduct()
                ?: throw NoSuchElementException("가장 최근 본 상품이 없습니다.")
        }
}
