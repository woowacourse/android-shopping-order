package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import woowacourse.shopping.data.source.local.recent.RecentProductDao
import woowacourse.shopping.data.source.local.recent.RecentProductEntity
import woowacourse.shopping.data.source.remote.ProductRemoteDataSource
import woowacourse.shopping.data.source.remote.dto.product.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepository(
    private val remoteProductDataSource: ProductRemoteDataSource,
    private val recentProductDao: RecentProductDao,
) : ProductRepository {
    private val _products = MutableStateFlow<List<Product>>(emptyList())

    override val products = _products.asStateFlow()

    override suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Int {
        val newProducts = remoteProductDataSource.fetchProducts(page, size).map { it.toDomain() }
        _products.update { products ->
            (products + newProducts).distinctBy { it.id }
        }
        return newProducts.size
    }

    override fun getRecentProductsStream(size: Int): Flow<List<Product>> =
        recentProductDao
            .getRecentStream(size)
            .map { entities ->
                entities.mapNotNull { getProductById(it.productId) }
            }

    override suspend fun upsertRecentProduct(id: Long) {
        recentProductDao.upsertRecentProduct(
            RecentProductEntity(
                productId = id,
                lastViewedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun getProductById(id: Long): Product? = _products.value.find { it.id == id }
}
