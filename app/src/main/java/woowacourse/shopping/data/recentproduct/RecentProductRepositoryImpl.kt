package woowacourse.shopping.data.recentproduct

import woowacourse.shopping.data.database.dao.RecentProductDao
import woowacourse.shopping.data.entity.RecentProductEntity
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.RecentProducts
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.data.product.ProductRemoteDataSourceOkhttp

class RecentProductRepositoryImpl(
    private val recentProductDao: RecentProductDao,
    private val productRemoteDataSource: ProductRemoteDataSourceOkhttp
) : RecentProductRepository {
    override fun getAll(onSuccess: (RecentProducts) -> Unit, onFailure: () -> Unit) {
        val recentProducts = mutableListOf<RecentProduct>()
        val entities = recentProductDao.selectAll()
        entities.forEach { entity ->
            productRemoteDataSource.getProduct(
                entity.id,
                onSuccess = { product ->
                    recentProducts.add(RecentProduct(entity.time, product))

                    if (entities.size == recentProducts.size) {
                        onSuccess(RecentProducts(recentProducts))
                    }
                },
                onFailure = { onFailure() }
            )
        }
    }

    override fun addRecentProduct(recentProduct: RecentProduct) {
        val recentProductEntity = RecentProductEntity(
            recentProduct.product.id,
            recentProduct.time
        )
        recentProductDao.insertRecentProduct(recentProductEntity)
    }

    override fun updateRecentProduct(recentProduct: RecentProduct) {
        val entity = RecentProductEntity(recentProduct.product.id, recentProduct.time)
        recentProductDao.updateRecentProduct(entity)
    }

    override fun getLatestRecentProduct(onSuccess: (RecentProduct?) -> Unit, onFailure: () -> Unit) {
        val entity = recentProductDao.selectLatestRecentProduct()
        if (entity == null) {
            onSuccess(null)
        } else {
            productRemoteDataSource.getProduct(
                entity.id,
                onSuccess = { onSuccess(RecentProduct(entity.time, it)) },
                onFailure = { onFailure() }
            )
        }
    }

    override fun isExist(id: Int): Boolean {
        return recentProductDao.selectProduct(id) != null
    }
}
