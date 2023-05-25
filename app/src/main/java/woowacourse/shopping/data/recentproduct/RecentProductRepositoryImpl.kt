package woowacourse.shopping.data.recentproduct

import woowacourse.shopping.Product
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.product.ProductRemoteDataSource
import woowacourse.shopping.repository.RecentProductRepository

class RecentProductRepositoryImpl constructor(
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
    private val productRemoteDataSource: ProductRemoteDataSource,
) : RecentProductRepository {
    override fun addRecentProductId(recentProductId: Int) {
        recentProductLocalDataSource.addRecentProduct(recentProductId)
    }

    override fun deleteRecentProductId(recentProductId: Int) {
        recentProductLocalDataSource.deleteRecentProduct(recentProductId)
    }

    override fun deleteAllProducts() {
        recentProductLocalDataSource.deleteAllProduct()
    }

    override fun getRecentProducts(size: Int): List<Product> {
        val recentProductIdList = recentProductLocalDataSource.getRecentProductIdList(size)
        return recentProductIdList.map {
            productRemoteDataSource.findProductById(it).toDomain()
        }
    }

    override fun getMostRecentProduct(): Product {
        val mostRecentProductId = recentProductLocalDataSource.getMostRecentProductId()
        if (mostRecentProductId == -1) return Product.defaultProduct
        return productRemoteDataSource.findProductById(mostRecentProductId).toDomain()
    }
}
