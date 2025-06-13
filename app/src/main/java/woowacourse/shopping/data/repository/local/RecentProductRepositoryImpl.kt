package woowacourse.shopping.data.repository.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.local.RecentProductDataSource
import woowacourse.shopping.data.entity.RecentlyViewedProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductDataSource: RecentProductDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RecentProductRepository {
    override suspend fun getRecentProducts(): Result<List<Product>> =
        withContext(defaultDispatcher) {
            runCatching {
                recentProductDataSource.getProducts().map { it.toDomain() }
            }
        }

    override suspend fun getMostRecentProduct(): Result<Product?> =
        withContext(defaultDispatcher) {
            runCatching {
                recentProductDataSource.getMostRecentProduct()?.toDomain()
            }
        }

    override suspend fun insertRecentProduct(product: Product): Result<Unit> =
        withContext(defaultDispatcher) {
            runCatching {
                val recentProducts = recentProductDataSource.getProducts()
                val productId = product.productId

                if (isNewProduct(recentProducts, productId) && recentProducts.size == 10) {
                    val oldProduct = recentProductDataSource.getOldestProduct()
                    recentProductDataSource.delete(oldProduct)
                }

                recentProductDataSource.insert(product.toEntity())
            }
        }

    private fun isNewProduct(
        recentProducts: List<RecentlyViewedProduct>,
        productId: Long,
    ): Boolean = recentProducts.none { it.productId == productId }

    private fun RecentlyViewedProduct.toDomain(): Product =
        Product(
            productId = this.productId,
            name = this.name,
            _price = Price(this.price),
            imageUrl = this.imageUrl,
            category = this.category,
        )

    private fun Product.toEntity(): RecentlyViewedProduct =
        RecentlyViewedProduct(
            productId = this.productId,
            viewedAt = System.currentTimeMillis(),
            name = this.name,
            price = this.price,
            imageUrl = this.imageUrl,
            category = this.category,
        )
}
