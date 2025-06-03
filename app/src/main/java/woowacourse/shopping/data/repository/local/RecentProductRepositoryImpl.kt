package woowacourse.shopping.data.repository.local

import woowacourse.shopping.data.datasource.local.RecentProductLocalDataSource
import woowacourse.shopping.data.entity.RecentlyViewedProduct
import woowacourse.shopping.data.entity.toData
import woowacourse.shopping.data.entity.toDomain
import woowacourse.shopping.data.runThread
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : RecentProductRepository {
    override fun getRecentProducts(onResult: (Result<List<Product>>) -> Unit) {
        runThread(
            block = {
                recentProductLocalDataSource
                    .getProducts()
                    .map { recentProduct -> recentProduct.toDomain() }
            },
            onResult = onResult,
        )
    }

    override fun getMostRecentProduct(onResult: (Result<Product?>) -> Unit) {
        runThread(
            block = { recentProductLocalDataSource.getMostRecentProduct()?.toDomain() },
            onResult = onResult,
        )
    }

    override fun insertRecentProduct(
        product: Product,
        onResult: (Result<Unit>) -> Unit,
    ) {
        runThread(
            block = {
                val recentProducts = recentProductLocalDataSource.getProducts()
                val productId = product.productId

                if (isNewProduct(recentProducts, productId) && recentProducts.size == 10) {
                    val oldProduct = recentProductLocalDataSource.getOldestProduct()
                    recentProductLocalDataSource.delete(oldProduct)
                }

                recentProductLocalDataSource.insert(product.toData())
                Result.success(Unit)
            },
            onResult = onResult,
        )
    }

    private fun isNewProduct(
        recentProducts: List<RecentlyViewedProduct>,
        productId: Long,
    ): Boolean = recentProducts.none { it.productId == productId }
}
