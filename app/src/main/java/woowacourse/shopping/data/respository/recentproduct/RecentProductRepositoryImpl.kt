package woowacourse.shopping.data.respository.recentproduct

import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSource
import woowacourse.shopping.data.respository.recentproduct.source.local.RecentProductLocalDataSource
import woowacouse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacouse.shopping.model.recentproduct.RecentProduct
import woowacouse.shopping.model.recentproduct.RecentProducts

class RecentProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
) : RecentProductRepository {

    override fun getRecentProducts(limit: Int, onSuccess: (RecentProducts) -> Unit) {
        productRemoteDataSource.requestDatas({}) { products ->
            val recentProductEntities = recentProductLocalDataSource.getAllRecentProducts(limit)
            val recentProducts = (
                    recentProductEntities.mapNotNull { recentProductEntity ->
                        products.find { it.id == recentProductEntity.productId }?.let { product ->
                            RecentProduct(recentProductEntity.id, product)
                        }
                    }
                ).toList()
            onSuccess(RecentProducts(recentProducts))
        }
    }

    override fun deleteNotTodayRecentProducts(today: String) {
        recentProductLocalDataSource.deleteNotToday(today)
    }

    override fun addCart(productId: Long) {
        recentProductLocalDataSource.insertRecentProduct(productId)
    }
}
